package fr.xavierristat.exercicespring.feature.product;

import fr.xavierristat.exercicespring.feature.database.Product;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("")
    public ResponseEntity<Product> insertNewProduct(@RequestBody Product produit) {
        Product createdProduct = productService.insertNewProduct(produit);
        if (createdProduct == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteNewProduct(@PathVariable Integer productId) {
        boolean isDeleted = productService.deleteProduct(productId);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("")
    public void deleteNewProduct(@RequestParam("productName") String productName) {

    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product produit, @PathVariable("productId") Integer productId){
        try{
            return ResponseEntity.ok(productService.updateProduct(productId, produit));
        } catch (EntityNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ConstraintViolationException constraintViolationException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
