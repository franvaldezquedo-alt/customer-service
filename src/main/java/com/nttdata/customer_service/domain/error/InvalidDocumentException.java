package com.nttdata.customer_service.domain.error;

public class InvalidDocumentException extends RuntimeException {
  public InvalidDocumentException(String message) {
    super(message);
  }
}
