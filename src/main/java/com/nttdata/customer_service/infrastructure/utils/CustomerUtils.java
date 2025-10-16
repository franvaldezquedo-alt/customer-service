package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerUtils {

    /**
     * Convierte una lista de entidades CustomerEntity a un objeto CustomerListResponse.
     * @param entity Lista de entidades CustomerEntity.
     * @return Objeto CustomerListResponse con los datos convertidos.
     */
    public static CustomerListResponse convertCustomerListResponse(List<CustomerEntity> entity) {
        return CustomerListResponse.builder()
                .data(entity.stream()
                        .map(CustomerUtils::convertToCustomerResponse)
                        .collect(Collectors.toList()))
                .build();
    }


    /**
     * Convierte una entidad CustomerEntity a un objeto Customer.
     * @param entity Entidad CustomerEntity.
     * @return Objeto Customer con los datos convertidos.
     */
    public static Customer convertToCustomerResponse(CustomerEntity entity){
        return Customer.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber())
                .customerType(entity.getCustomerType())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
