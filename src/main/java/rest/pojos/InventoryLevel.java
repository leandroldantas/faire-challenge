package rest.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;

public class InventoryLevel implements Serializable {

    private String sku;

    @JsonProperty("current_quantity")
    private Long currentQuantity;

    private Boolean discontinued;

    @JsonProperty("backordered_until")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar backorderedUntil;

    public InventoryLevel() {
    }

    public InventoryLevel(String sku, Long currentQuantity, Boolean discontinued, Calendar backorderedUntil) {
        this.sku = sku;
        this.currentQuantity = currentQuantity;
        this.discontinued = discontinued;
        this.backorderedUntil = backorderedUntil;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Long currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Boolean getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued;
    }

    public Calendar getBackorderedUntil() {
        return backorderedUntil;
    }

    public void setBackorderedUntil(Calendar backorderedUntil) {
        this.backorderedUntil = backorderedUntil;
    }
}
