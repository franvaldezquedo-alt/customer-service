package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CustomerResponseMapper {

  /**
   * Convierte una lista de Customer a CustomerListResponse
   */
  public CustomerListResponse toCustomerListResponse(List<Customer> customers) {
    if (customers == null || customers.isEmpty()) {
      return CustomerListResponse.builder()
            .data(Collections.emptyList())
            .error(null)
            .build();
    }

    return CustomerListResponse.builder()
          .data(new ArrayList<>(customers))
          .error(null)
          .build();
  }

  /**
   * Convierte un solo Customer a una lista de respuesta (CustomerListResponse con un elemento)
   */
  public CustomerListResponse toSingletonResponse(Customer customer) {
    if (customer == null) {
      return CustomerListResponse.builder()
            .data(Collections.emptyList())
            .error(null)
            .build();
    }

    return CustomerListResponse.builder()
          .data(Collections.singletonList(customer))
          .error(null)
          .build();
  }

  /**
   * Convierte un Customer (creado o actualizado) en una respuesta simple de éxito
   */
  public CustomerResponse toSuccessResponse(String codEntity, String message) {
    return CustomerResponse.builder()
          .codResponse(0)
          .messageResponse(message)
          .codEntity(codEntity)
          .build();
  }

  /**
   * Convierte una operación de eliminación en una respuesta de éxito
   */
  public CustomerResponse toDeleteResponse(String id) {
    return CustomerResponse.builder()
          .codResponse(0)
          .messageResponse("Cliente eliminado correctamente")
          .codEntity(id)
          .build();
  }

  /**
   * Convierte un error o excepción en una respuesta estándar
   */
  public CustomerResponse toErrorResponse(String message) {
    return CustomerResponse.builder()
          .codResponse(1)
          .messageResponse(message)
          .codEntity(null)
          .build();
  }
}
