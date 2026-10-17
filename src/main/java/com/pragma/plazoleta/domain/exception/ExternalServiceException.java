package com.pragma.plazoleta.domain.exception;

public class ExternalServiceException extends DomainException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
