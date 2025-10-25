package com.nttdata.customer_service.domain.error;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String id) {
        super("Cliente con ID " + id + " no encontrado.");
    }
}
