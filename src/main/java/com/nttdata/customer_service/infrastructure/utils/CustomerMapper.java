package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.StatusType;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Componente Mapper para convertir entre modelos de dominio Customer, entidades y DTOs.
 * <p>
 * Este componente proporciona funcionalidad de mapeo bidireccional entre la capa de dominio
 * (Customer) y la capa de infraestructura (CustomerEntity), así como la conversión desde
 * DTOs de petición a modelos de dominio.
 * </p>
 *
 * @author NTT Data
 * @version 1.0
 */
@Component
public class CustomerMapper {

  /**
   * Convierte un modelo de dominio Customer a una entidad CustomerEntity para persistencia.
   *
   * @param customer el modelo de dominio a convertir
   * @return la CustomerEntity correspondiente
   */
  public CustomerEntity toEntity(Customer customer) {
    return CustomerEntity.builder()
          .id(customer.getId())
          .documentType(customer.getDocumentType())
          .documentNumber(customer.getDocumentNumber())
          .fullName(customer.getFullName())
          .businessName(customer.getBusinessName())
          .email(customer.getEmail())
          .phoneNumber(customer.getPhoneNumber())
          .address(customer.getAddress())
          .customerType(customer.getCustomerType())
          .createdAt(customer.getCreatedAt())
          .updatedAt(customer.getUpdatedAt())
          .status(customer.getStatus())
          .build();
  }

  /**
   * Convierte una entidad CustomerEntity de la base de datos a un modelo de dominio Customer.
   *
   * @param entity la entidad a convertir
   * @return el modelo de dominio Customer correspondiente
   */
  public Customer toDomain(CustomerEntity entity) {
    return Customer.builder()
          .id(entity.getId())
          .documentType(entity.getDocumentType())
          .documentNumber(entity.getDocumentNumber())
          .fullName(entity.getFullName())
          .businessName(entity.getBusinessName())
          .email(entity.getEmail())
          .phoneNumber(entity.getPhoneNumber())
          .address(entity.getAddress())
          .customerType(entity.getCustomerType())
          .createdAt(entity.getCreatedAt())
          .updatedAt(entity.getUpdatedAt())
          .status(entity.getStatus())
          .build();
  }

  /**
   * Convierte un DTO CustomerRequest a un modelo de dominio Customer para creación.
   * <p>
   * Este método inicializa las marcas de tiempo createdAt y updatedAt con la hora actual
   * y establece el estado como ACTIVE por defecto.
   * </p>
   *
   * @param customerRequest el DTO de petición que contiene los datos del cliente
   * @return un nuevo modelo de dominio Customer listo para ser persistido
   */
  public Customer fromRequest(CustomerRequest customerRequest) {
    return Customer.builder()
          .documentType(customerRequest.getDocumentType())
          .documentNumber(customerRequest.getDocumentNumber())
          .fullName(customerRequest.getFullName())
          .businessName(customerRequest.getBusinessName())
          .email(customerRequest.getEmail())
          .phoneNumber(customerRequest.getPhoneNumber())
          .address(customerRequest.getAddress())
          .customerType(customerRequest.getCustomerType())
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .status(StatusType.ACTIVE)
          .build();
  }

  /**
   * Actualiza un modelo de dominio Customer existente con datos de un CustomerRequest.
   * <p>
   * Este método preserva el ID original y la marca de tiempo createdAt mientras actualiza
   * todos los demás campos con los nuevos datos y establece updatedAt con la hora actual.
   * </p>
   *
   * @param existing el Customer existente a actualizar
   * @param request  el DTO de petición que contiene los nuevos datos del cliente
   * @return un modelo de dominio Customer actualizado
   */
  public Customer updateFromRequest(Customer existing, CustomerRequest request) {
    return Customer.builder()
          .id(existing.getId())
          .documentType(request.getDocumentType())
          .documentNumber(request.getDocumentNumber())
          .fullName(request.getFullName())
          .businessName(request.getBusinessName())
          .email(request.getEmail())
          .phoneNumber(request.getPhoneNumber())
          .address(request.getAddress())
          .customerType(request.getCustomerType())
          .createdAt(existing.getCreatedAt())
          .updatedAt(LocalDateTime.now())
          .status(StatusType.ACTIVE)
          .build();
  }

  /**
   * Convierte una lista de modelos de dominio Customer a un CustomerListResponse.
   * <p>
   * Si la lista proporcionada es null, se utiliza una lista vacía en su lugar para
   * garantizar una respuesta no nula.
   * </p>
   *
   * @param customers la lista de clientes a incluir en la respuesta
   * @return un CustomerListResponse que contiene los datos de los clientes
   */
  public CustomerListResponse toCustomerListResponse(List<Customer> customers) {
    return CustomerListResponse.builder()
          .data(customers != null ? customers : Collections.emptyList())
          .error(null)
          .build();
  }
}