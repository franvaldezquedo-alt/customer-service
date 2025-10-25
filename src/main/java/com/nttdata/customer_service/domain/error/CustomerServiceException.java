package com.nttdata.customer_service.domain.error;

public class CustomerServiceException extends RuntimeException {
  public CustomerServiceException(String message) {
    super(message);
  }

  public CustomerServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
