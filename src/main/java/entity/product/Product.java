package entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.product.option.ProductOption;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;

public class Product implements Serializable {

    private String id;

    @JsonProperty("brand_id")
    private String brandId;

    @JsonProperty("short_description")
    private String shortDescription;

    private String description;

    @JsonProperty("wholesale_price_cents")
    private Long wholesalePriceCents;

    @JsonProperty("retail_price_cents")
    private Long retailPriceCents;

    private Boolean active;

    private String name;

    @JsonProperty("unit_multiplier")
    private Integer unitMultiplier;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar updatedAt;

    private Collection<ProductOption> options;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getWholesalePriceCents() {
        return wholesalePriceCents;
    }

    public void setWholesalePriceCents(Long wholesalePriceCents) {
        this.wholesalePriceCents = wholesalePriceCents;
    }

    public Long getRetailPriceCents() {
        return retailPriceCents;
    }

    public void setRetailPriceCents(Long retailPriceCents) {
        this.retailPriceCents = retailPriceCents;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUnitMultiplier() {
        return unitMultiplier;
    }

    public void setUnitMultiplier(Integer unitMultiplier) {
        this.unitMultiplier = unitMultiplier;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collection<ProductOption> getOptions() {
        return options;
    }

    public void setOptions(Collection<ProductOption> options) {
        this.options = options;
    }
}
