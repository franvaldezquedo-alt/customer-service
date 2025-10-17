package com.nttdata.customer_service.domain.error;

public class CustomerException extends RuntimeException {
  public CustomerException(String message) {
    super(message);
  }
}
