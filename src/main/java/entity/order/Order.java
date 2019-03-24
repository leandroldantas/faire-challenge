package entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import entity.address.Address;
import entity.shipment.Shipment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class Order implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar updatedAt;

    @JsonProperty("ship_after")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd'T'HHmmss.SSSXXX")
    private Calendar shipAfter;

    @JsonProperty("state")
    private OrderState state;

    @JsonProperty("items")
    private List<ItemOrder> items;

    @JsonProperty("shipments")
    private List<Shipment> shipments;

    @JsonProperty("address")
    private Address address;

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

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public List<ItemOrder> getItems() {
        return items;
    }

    public void setItems(List<ItemOrder> items) {
        this.items = items;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Calendar getShipAfter() {
        return shipAfter;
    }

    public void setShipAfter(Calendar shipAfter) {
        this.shipAfter = shipAfter;
    }
}
