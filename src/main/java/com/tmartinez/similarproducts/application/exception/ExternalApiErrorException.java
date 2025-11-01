package com.tmartinez.similarproducts.application.exception;

public class ExternalApiErrorException extends ExternalApiException {
    public ExternalApiErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
