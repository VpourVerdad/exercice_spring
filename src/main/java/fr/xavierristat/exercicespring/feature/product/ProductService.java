package fr.xavierristat.exercicespring.feature.product;

import fr.xavierristat.exercicespring.feature.database.Product;
import fr.xavierristat.exercicespring.feature.database.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    public Product insertNewProduct(Product produit) {
        try {
            return productRepository.save(produit);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            return null;
        }
    }

    public boolean deleteProduct(Integer productId) {
        try {
            productRepository.deleteById(productId);
            return true;
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return false;
        }
    }

    public Product updateProduct(Integer productId, Product produit) throws EntityNotFoundException, ConstraintViolationException {
        Optional<Product> byId = productRepository.findById(productId);
        if (byId.isEmpty()) {
            throw new EntityNotFoundException("Product with Id %d not exist".formatted(productId));
        }

        List<Product> byName = productRepository.findByNameAndIdNot(produit.getName(), productId);
        if (byName.size() > 0) {
            throw new ConstraintViolationException("Name for product " + produit.getName() + " already exist", new SQLException(), "products::name INDEX UNIQUE");
        }

        Product findProduct = byId.get();
        findProduct.setName(produit.getName());
        findProduct.setDescription(produit.getDescription());
        return productRepository.save(findProduct);

    }
}
