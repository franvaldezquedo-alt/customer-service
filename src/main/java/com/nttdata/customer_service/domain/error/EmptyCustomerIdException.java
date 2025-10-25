package com.nttdata.customer_service.domain.error;

public class EmptyCustomerIdException extends RuntimeException {
  public EmptyCustomerIdException(String message) {
    super(message);
  }
}
