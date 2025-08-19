package com.flrxnt.order.exception;

/**
 * Exception levée lorsqu'un produit n'est pas valide ou n'existe pas
 */
public class ProductNotValidException extends RuntimeException {

    private final Long productId;

    public ProductNotValidException(String message) {
        super(message);
        this.productId = null;
    }

    public ProductNotValidException(String message, Long productId) {
        super(message);
        this.productId = productId;
    }

    public ProductNotValidException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
    }

    public ProductNotValidException(String message, Long productId, Throwable cause) {
        super(message, cause);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "ProductNotValidException{" +
                "productId=" + productId +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
