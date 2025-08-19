package com.flrxnt.order.exception;

/**
 * Exception levée lorsqu'un client n'est pas valide ou n'existe pas
 */
public class ClientNotValidException extends RuntimeException {

    private final Long clientId;

    public ClientNotValidException(String message) {
        super(message);
        this.clientId = null;
    }

    public ClientNotValidException(String message, Long clientId) {
        super(message);
        this.clientId = clientId;
    }

    public ClientNotValidException(String message, Throwable cause) {
        super(message, cause);
        this.clientId = null;
    }

    public ClientNotValidException(String message, Long clientId, Throwable cause) {
        super(message, cause);
        this.clientId = clientId;
    }

    public Long getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "ClientNotValidException{" +
                "clientId=" + clientId +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
