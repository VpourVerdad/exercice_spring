package fr.xavierristat.exercicespring.feature.database;

import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductWithPriceDTO extends ProductDTO{

    private BigDecimal unitPrice;
    public ProductWithPriceDTO(Integer id, String name, String description, BigDecimal unitPrice) {
        super(id, name, description);
        this.unitPrice = unitPrice;
    }

    public ProductWithPriceDTO(Tuple tuple) {
        super(tuple);
        this.unitPrice = BigDecimal.valueOf((Double) tuple.get(3));
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        ProductWithPriceDTO that = (ProductWithPriceDTO) o;
        return unitPrice.equals(that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unitPrice);
    }
}
