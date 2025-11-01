package com.tmartinez.similarproducts.application.exception;

public class ExternalApiTimeoutException extends ExternalApiException {
    public ExternalApiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
