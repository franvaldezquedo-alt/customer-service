package com.nttdata.customer_service.infrastructure.controller;

import com.nttdata.customer_service.domain.error.*;
import com.nttdata.customer_service.domain.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ============================================================
  // EXCEPCIONES PERSONALIZADAS - 404 NOT FOUND
  // ============================================================

  @ExceptionHandler(CustomerNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerNotFound(
        CustomerNotFoundException ex,
        ServerWebExchange exchange) {

    log.error("Cliente no encontrado: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.NOT_FOUND.value())
          .error(HttpStatus.NOT_FOUND.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
  }

  // ============================================================
  // EXCEPCIONES PERSONALIZADAS - 400 BAD REQUEST
  // ============================================================

  @ExceptionHandler(InvalidCustomerIdException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInvalidCustomerId(
        InvalidCustomerIdException ex,
        ServerWebExchange exchange) {

    log.error("ID de cliente inválido: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(InvalidDocumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInvalidDocument(
        InvalidDocumentException ex,
        ServerWebExchange exchange) {

    log.error("Documento inválido: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(EmptyCustomerIdException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleEmptyCustomerId(
        EmptyCustomerIdException ex,
        ServerWebExchange exchange) {

    log.error("ID de cliente vacío: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  // ============================================================
  // EXCEPCIONES PERSONALIZADAS - 409 CONFLICT
  // ============================================================

  @ExceptionHandler(CustomerAlreadyExistsException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerAlreadyExists(
        CustomerAlreadyExistsException ex,
        ServerWebExchange exchange) {

    log.error("Cliente ya existe: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.CONFLICT.value())
          .error(HttpStatus.CONFLICT.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
  }

  @ExceptionHandler(CustomerAlreadyInactiveException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerAlreadyInactive(
        CustomerAlreadyInactiveException ex,
        ServerWebExchange exchange) {

    log.error("Cliente ya está inactivo: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.CONFLICT.value())
          .error(HttpStatus.CONFLICT.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
  }

  // ============================================================
  // VALIDACIONES DE BEAN VALIDATION (@Valid)
  // ============================================================

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(
        WebExchangeBindException ex,
        ServerWebExchange exchange) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    log.error("Error de validación: {}", errors);

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message("Error de validación en los campos: " + errors.toString())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  // ============================================================
  // EXCEPCIONES GENÉRICAS
  // ============================================================

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(
        IllegalArgumentException ex,
        ServerWebExchange exchange) {

    log.error("Argumento ilegal: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.BAD_REQUEST.value())
          .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
  }

  @ExceptionHandler(CustomerServiceException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerServiceException(
        CustomerServiceException ex,
        ServerWebExchange exchange) {

    log.error("Error en el servicio de clientes: {}", ex.getMessage(), ex);

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
          .message(ex.getMessage())
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
  }

  // ============================================================
  // EXCEPTION CATCH-ALL (500 Internal Server Error)
  // ============================================================

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
        Exception ex,
        ServerWebExchange exchange) {

    log.error("Error inesperado: {}", ex.getMessage(), ex);

    ErrorResponse error = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
          .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
          .message("Ha ocurrido un error inesperado en el servidor")
          .path(exchange.getRequest().getPath().value())
          .build();

    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
  }
}