package rest.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;

public class ItemAwaitAvailability implements Serializable {

    @JsonIgnore
    private String name;

    @JsonProperty("available_quantity")
    private Long availableQuantity;

    private Boolean discontinued;

    @JsonProperty("backordered_until")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar backorderedUntil;

    public ItemAwaitAvailability() {
    }

    public ItemAwaitAvailability(String name, Long availableQuantity, Boolean discontinued, Calendar backorderedUntil) {
        this.name = name;
        this.availableQuantity = availableQuantity;
        this.discontinued = discontinued;
        this.backorderedUntil = backorderedUntil;
    }

    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) {
        this.availableQuantity = availableQuantity;
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

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }
}
