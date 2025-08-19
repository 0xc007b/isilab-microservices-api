package com.flrxnt.customer.exception;

public class CustomerNotFoundException extends RuntimeException {

    private final Long customerId;

    public CustomerNotFoundException(Long customerId) {
        super(String.format("Customer with ID %d not found", customerId));
        this.customerId = customerId;
    }

    public CustomerNotFoundException(String email) {
        super(String.format("Customer with email '%s' not found", email));
        this.customerId = null;
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.customerId = null;
    }

    public CustomerNotFoundException(Long customerId, String message) {
        super(message);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
