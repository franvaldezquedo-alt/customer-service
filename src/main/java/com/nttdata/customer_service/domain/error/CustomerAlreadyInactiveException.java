package com.nttdata.customer_service.domain.error;

import lombok.Getter;

@Getter
public class CustomerAlreadyInactiveException extends RuntimeException {
  private final String customerId;

  public CustomerAlreadyInactiveException(String customerId) {
    super(String.format("El cliente con id %s ya está inactivo", customerId));
    this.customerId = customerId;
  }

}
