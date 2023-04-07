package fr.xavierristat.exercicespring.feature.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("select p from Product p where p.category.categoryName = ?1")
    List<Product> findByCategoryName(String categoryName);

    List<Product> findByNameAndIdNot(String name, Integer id);

}
