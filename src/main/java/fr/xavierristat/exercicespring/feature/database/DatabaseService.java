package fr.xavierristat.exercicespring.feature.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;


    public List<String> getListProductNames() {
        String request = "select name from products;";
        Query query = entityManager.createNativeQuery(request, Tuple.class);
        List<Tuple> resultList = query.getResultList();
        List<String> collect = resultList.stream().map(tuple -> (String) tuple.get(0)).collect(Collectors.toList());

        return collect;
    }

    /*
    List<String> getProductNameList() {
        Query query = entityManager.createNativeQuery("SELECT name FROM products;", Tuple.class);
        List<Tuple> results = (List<Tuple>) query.getResultList();

        return results.stream()
                .map(tuple -> (String) tuple.get(0))
                .collect(Collectors.toList());
    }


     */
    public List<ProductDTO> getListProduct() {
        String request = "select id, name, description from products;";
        Query query = entityManager.createNativeQuery(request, Tuple.class);
        List<Tuple> resultList = query.getResultList();
        List<ProductDTO> collect = resultList.stream().map(ProductDTO::new).toList();
        return collect;
    }

    public List<ProductWithPriceDTO> getListProductWithPrices() {
        String request = "select id, name, description, unit_price_ht from products;";
        Query query = entityManager.createNativeQuery(request, Tuple.class);
        List<Tuple> resultList = query.getResultList();
        List<ProductWithPriceDTO> collect = resultList.stream().map(ProductWithPriceDTO::new).toList();
        return collect;
    }

    public List<Product> getListProductFromEntity() {
        return productRepository.findAll();
    }

    public ProductWithPriceDTO getOneProduct(Integer id) {
        String request = "select id, name, description, unit_price_ht from products where id = :productId";
        Query query = entityManager.createNativeQuery(request, Tuple.class)
                .setParameter("productId", id);
        Tuple result = (Tuple) query.getSingleResult();
        return new ProductWithPriceDTO(result);
    }

    public Product getOneProductEntity(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductEntityByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ProductWithPriceDTO> getProductByName(String name) {
        String request = "select id, name, description, unit_price_ht from products where name LIKE :name";
        Query query = entityManager.createNativeQuery(request, Tuple.class)
                .setParameter("name", "%"+name+"%");
        List<Tuple> resultList = (List<Tuple>) query.getResultList();
        return resultList.stream().map(ProductWithPriceDTO::new).toList();
    }

    public List<ProductWithPriceDTO> getProductByCategory(String categoryName) {
        String request = "select id, name, description, unit_price_ht from products inner join categories on categories.category_id = products.category_id where category_name = :categoryName";
        Query query = entityManager.createNativeQuery(request, Tuple.class)
                .setParameter("categoryName", categoryName);
        List<Tuple> resultList = (List<Tuple>) query.getResultList();
        return resultList.stream().map(ProductWithPriceDTO::new).toList();
    }

    public List<Product> getProductEntityByCategoryName(String name) {
        return productRepository.findByCategoryName(name);
    }
}
