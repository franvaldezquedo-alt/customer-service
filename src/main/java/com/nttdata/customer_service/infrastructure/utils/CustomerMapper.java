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

@Component
public class CustomerMapper {

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

  /* Convierte un CustomerRequest a un Customer (para creación) */
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

  public CustomerListResponse toCustomerListResponse(List<Customer> customers) {
    return CustomerListResponse.builder()
          .data(customers != null ? customers : Collections.emptyList())
          .error(null)
          .build();
  }
}
