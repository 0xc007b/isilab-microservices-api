package com.flrxnt.customer.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    private final String email;

    public CustomerAlreadyExistsException(String email) {
        super(String.format("Customer with email '%s' already exists", email));
        this.email = email;
    }

    public CustomerAlreadyExistsException(String email, String message) {
        super(message);
        this.email = email;
    }

    public CustomerAlreadyExistsException(String email, String message, Throwable cause) {
        super(message, cause);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
