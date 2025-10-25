package com.nttdata.customer_service.domain.error;

public class InvalidCustomerIdException extends RuntimeException {
  public InvalidCustomerIdException(String message) {
    super(message);
  }
}
