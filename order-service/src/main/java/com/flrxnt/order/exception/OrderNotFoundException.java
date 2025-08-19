package com.flrxnt.order.exception;

/**
 * Exception levée lorsqu'une commande n'est pas trouvée
 */
public class OrderNotFoundException extends RuntimeException {

    private final Long orderId;

    public OrderNotFoundException(String message) {
        super(message);
        this.orderId = null;
    }

    public OrderNotFoundException(String message, Long orderId) {
        super(message);
        this.orderId = orderId;
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.orderId = null;
    }

    public OrderNotFoundException(String message, Long orderId, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "OrderNotFoundException{" +
                "orderId=" + orderId +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
