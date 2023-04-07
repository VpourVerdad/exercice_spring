package fr.xavierristat.exercicespring.feature.database;

import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class ProductDTO {

    private Integer id;
    private String name;
    private String description;


    public ProductDTO(Integer id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ProductDTO(Tuple tuple){
        this.id = (Integer) tuple.get(0);
        this.name = (String) tuple.get(1);
        this.description = (String) tuple.get(2);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
