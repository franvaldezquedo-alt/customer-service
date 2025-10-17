package com.nttdata.customer_service.domain.error;

public class GatewayTimeOutExceptions extends RuntimeException {
    public GatewayTimeOutExceptions(String message) {
        super(message);
    }
}
