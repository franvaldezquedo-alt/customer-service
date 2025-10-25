package com.nttdata.customer_service.domain.error;

import lombok.Getter;

@Getter
public class CustomerAlreadyExistsException extends RuntimeException {
  private final String documentType;
  private final String documentNumber;

  public CustomerAlreadyExistsException(String documentType, String documentNumber) {
    super(String.format("Cliente ya existe con documento %s: %s", documentType, documentNumber));
    this.documentType = documentType;
    this.documentNumber = documentNumber;
  }

}
