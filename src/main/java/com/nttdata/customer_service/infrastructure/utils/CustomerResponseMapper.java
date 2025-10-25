package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper responsable de transformar entidades del dominio Customer
 * a diferentes modelos de respuesta utilizados en la capa de infraestructura.
 */
@Component
public class CustomerResponseMapper {

  /**
   * Convierte una lista de clientes en una respuesta CustomerListResponse.
   * Si la lista es nula o vacía, retorna una lista vacía.
   */
  public CustomerListResponse toCustomerListResponse(List<Customer> customers) {
    List<Customer> safeList = (customers == null || customers.isEmpty())
          ? Collections.emptyList()
          : List.copyOf(customers);

    return CustomerListResponse.builder()
          .data(safeList)
          .build();
  }

  /**
   * Convierte un solo cliente en una respuesta CustomerListResponse con un solo elemento.
   * Si el cliente es nulo, devuelve una lista vacía.
   */
  public CustomerListResponse toSingletonResponse(Customer customer) {
    List<Customer> singletonList = (customer == null)
          ? Collections.emptyList()
          : Collections.singletonList(customer);

    return CustomerListResponse.builder()
          .data(singletonList)
          .build();
  }

  /**
   * Construye una respuesta de éxito genérica para operaciones de creación o actualización.
   */
  public CustomerResponse toSuccessResponse(String codEntity, String message) {
    return CustomerResponse.builder()
          .codResponse(Constants.SUCCESS_CODE)
          .messageResponse(message)
          .codEntity(codEntity)
          .build();
  }

  /**
   * Construye una respuesta de éxito para operaciones de eliminación.
   */
  public CustomerResponse toDeleteResponse(String id) {
    return CustomerResponse.builder()
          .codResponse(Constants.SUCCESS_CODE)
          .messageResponse(Constants.CUSTOMER_DELETED)
          .codEntity(id)
          .build();
  }

  /**
   * Construye una respuesta estándar de error.
   */
  public CustomerResponse toErrorResponse(String message) {
    return CustomerResponse.builder()
          .codResponse(1)
          .messageResponse(message)
          .codEntity(null)
          .build();
  }
}
