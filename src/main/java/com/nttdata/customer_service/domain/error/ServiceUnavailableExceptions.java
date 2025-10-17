package com.nttdata.customer_service.domain.error;

public class ServiceUnavailableExceptions extends RuntimeException {
    public ServiceUnavailableExceptions(String message) {
        super(message);
    }
}
