package fr.xavierristat.exercicespring;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xavierristat.exercicespring.feature.database.DatabaseService;
import fr.xavierristat.exercicespring.feature.database.Product;
import fr.xavierristat.exercicespring.feature.database.ProductDTO;
import fr.xavierristat.exercicespring.feature.database.ProductWithPriceDTO;
import io.micrometer.common.util.internal.logging.InternalLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExercicespringApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;
	private Logger logger = LoggerFactory.getLogger(ExercicespringApplicationTests.class);

	@Autowired
	private DatabaseService databaseService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {

	}

	@Test
	void helloWorldApiTest() throws Exception {
		mockMvc.perform(get("/api/hello/world")
						.contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(content().string("Hello World!"));
	}

	@Test
	void testDatabase() {
		Query query = entityManager.createNativeQuery("show tables;");
		List<String> results = ((List<String>) query.getResultList());
		String resultList = String.join(" - ", results);
		logger.info("Connexion à la BDD :: SUCCESS");
		logger.info("Liste des tables = [{}]", resultList);
	}

	@Test
	void getProductListTest() {
		Query query = entityManager.createNativeQuery("select name from products;", Tuple.class);
		List<Tuple> results = ((List<Tuple>) query.getResultList());
		List<String> productNames = results.stream().map(tuple -> (String) tuple.get(0)).toList();
		assertTrue(productNames.contains("Pomme"));
		assertTrue(productNames.contains("Télévision"));
	}

	@Test
	void testProductNames() {
		List<String> expectedNames = Arrays.asList("Pomme", "Télévision");

		List<String> listProductNames = databaseService.getListProductNames();
		assert listProductNames.containsAll(expectedNames);
	}

	@Test
	void testProductList() {
		ProductDTO p1 = new ProductDTO(1, "Pomme", "Pomme Golden");
		ProductDTO p2 = new ProductDTO(2, "Télévision", "Télévision 42 pouces");
		List<ProductDTO> listProduits = databaseService.getListProduct();
		assert listProduits.containsAll(Arrays.asList(p1, p2));
	}

	@Test
	void testProductListPrices() {
		Map<Integer, BigDecimal> listPrices = new HashMap<>();
		listPrices.put(1, BigDecimal.valueOf(0.50));

		List<ProductWithPriceDTO> listProduits = databaseService.getListProductWithPrices();

		assert listProduits.stream()
				.filter(produit -> listPrices.containsKey(produit.getId()))
				.allMatch(produit -> produit.getUnitPrice().equals(listPrices.get(produit.getId()))
				);
	}

	@Test
	void testProductFromEntity() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO p2 = new ProductWithPriceDTO(2, "Télévision", "Télévision 42 pouces", BigDecimal.valueOf(300.00));

		List<Product> listProductFromEntity = databaseService.getListProductFromEntity();

		assert listProductFromEntity.stream().allMatch(produit -> testEquality(produit, p1) || testEquality(produit, p2));
	}

	private boolean testEquality(Product produitEntity, ProductWithPriceDTO productWithPriceDTO) {
		return Objects.equals(produitEntity.getName(), productWithPriceDTO.getName()) && Objects.equals(produitEntity.getDescription(), productWithPriceDTO.getDescription()) && Objects.equals(BigDecimal.valueOf(produitEntity.getUnitPrice()), productWithPriceDTO.getUnitPrice());
	}

	@Test
	void testGetApple() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO oneProduct = databaseService.getOneProduct(p1.getId());
		assert oneProduct.equals(p1);
	}

	@Test
	void testGetAppleFromEntity() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		Product oneProduct = databaseService.getOneProductEntity(p1.getId());
		assert testEquality(oneProduct, p1);
	}

	@Test
	void testGetAppleByName() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO p2 = new ProductWithPriceDTO(3, "Pomme rouge", "Pink Lady", BigDecimal.valueOf(0.60));

		List<ProductWithPriceDTO> listProduits = databaseService.getProductByName("Pomme");
		assert listProduits.stream().allMatch(produit -> produit.equals(p1) || produit.equals(p2));
	}

	@Test
	void testGetAppleByCategory() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO p2 = new ProductWithPriceDTO(3, "Pomme rouge", "Pink Lady", BigDecimal.valueOf(0.60));

		List<ProductWithPriceDTO> listProduits = databaseService.getProductByCategory("Alimentaire");
		assert listProduits.stream().allMatch(produit -> produit.equals(p1) || produit.equals(p2));
	}

	@Test
	void testGetAppleEntitiesByName() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO p2 = new ProductWithPriceDTO(3, "Pomme rouge", "Pink Lady", BigDecimal.valueOf(0.60));

		List<Product> listProduits = databaseService.getProductEntityByName("Pomme");
		assert listProduits.stream().allMatch(produit -> testEquality(produit, p1) || testEquality(produit, p2));
	}

	@Test
	void testGetAppleEntitiesByCategoryName() {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(1, "Pomme", "Pomme Golden", BigDecimal.valueOf(0.50));
		ProductWithPriceDTO p2 = new ProductWithPriceDTO(3, "Pomme rouge", "Pink Lady", BigDecimal.valueOf(0.60));

		List<Product> listProduits = databaseService.getProductEntityByCategoryName("Alimentaire");
		assert listProduits.stream().allMatch(produit -> testEquality(produit, p1) || testEquality(produit, p2));
	}

	@Test
	void testInsertProduit() throws Exception {
		ProductWithPriceDTO p1 = new ProductWithPriceDTO(null, "Table", "table simple", BigDecimal.valueOf(100.00));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/product").content("{\"name\": \"table\",\n" +
				"\"description\" : \"table simple\",\n" +
				"\"unitPrice\" : 100.00,\n" +
				"\"categoryId\" : 3}").contentType(MediaType.APPLICATION_JSON);

		ResultMatcher resultStatus = MockMvcResultMatchers.status().isOk();
		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

	@Test
	void testDeleteProduct() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/product/5");
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isOk();
		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

	@Test
	void testDeleteProductByName() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/product?productName=table");
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isOk();
		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

	@Test
	void testUpdateProduct() throws Exception {

		Map<String, String> listFields = new HashMap<>(){{
			put("description", "console");
			put("name", "Playstation 5");
		}};


		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/product/7")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(listFields));
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isNotFound();

		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

	@Test
	void testUpdateProductWithExistingName() throws Exception{
		Map<String, String> listFields = new HashMap<>(){{
			put("description", "console");
			put("name", "Télévision");
		}};

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/product/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(listFields));
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isConflict();

		mockMvc.perform(requestBuilder)
				.andExpect(resultStatus);
	}

	@Test
	void testUpdateProductWithoutChangingName() throws Exception {
		Map<String, Object> updatedFields = new HashMap<>(){{
			put("description", "Updated Pomme Golden");
		}};

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/product/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedFields));
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isOk();

		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

	@Test
	void testUpdateProductWithUniqueName() throws Exception {
		Map<String, Object> updatedFields = new HashMap<>(){{
			put("name", "Pomme Verte");
			put("description", "Pomme Granny Smith");
		}};

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/product/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedFields));
		ResultMatcher resultStatus = MockMvcResultMatchers.status().isOk();

		mockMvc.perform(requestBuilder).andExpect(resultStatus);
	}

}
