package com.nttdata.customer_service.domain.error;

public class CustomerNotFoundExeptions extends RuntimeException {
  public CustomerNotFoundExeptions(String message) {
    super(message);
  }
}
