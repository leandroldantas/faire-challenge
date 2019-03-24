package entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;

public class ItemOrder implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar updatedAt;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_option_id")
    private String productOptionId;

    private Long quantity;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price_cents")
    private Long priceCents;

    @JsonProperty("tester_price_cents")
    private Long testerPriceCents;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_option_name")
    private String productOptionName;

    @JsonProperty("includes_tester")
    private Boolean includesTester;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductOptionId() {
        return productOptionId;
    }

    public void setProductOptionId(String productOptionId) {
        this.productOptionId = productOptionId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Long priceCents) {
        this.priceCents = priceCents;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductOptionName() {
        return productOptionName;
    }

    public void setProductOptionName(String productOptionName) {
        this.productOptionName = productOptionName;
    }

    public Boolean getIncludesTester() {
        return includesTester;
    }

    public void setIncludesTester(Boolean includesTester) {
        this.includesTester = includesTester;
    }

    public Long getTesterPriceCents() {
        return testerPriceCents;
    }

    public void setTesterPriceCents(Long testerPriceCents) {
        this.testerPriceCents = testerPriceCents;
    }
}
