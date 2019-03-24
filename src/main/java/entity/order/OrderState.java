package entity.order;

public enum OrderState {

    NEW,
    PROCESSING,
    PRE_TRANSIT,
    IN_TRANSIT,
    DELIVERED,
    BACKORDERED,
    CANCELED;

}
