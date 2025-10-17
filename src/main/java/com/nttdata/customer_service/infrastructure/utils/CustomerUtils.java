package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.error.CustomerErrorFactory;
import com.nttdata.customer_service.domain.model.*;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerUtils {


    public static CustomerListResponse convertCustomerListResponse(List<CustomerEntity> entity) {
        return CustomerListResponse.builder()
                .data(entity.stream()
                        .map(CustomerUtils::convertToCustomerResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Customer convertToCustomerResponse(CustomerEntity entity){
        return Customer.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .businessName(entity.getBusinessName())
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

    public static CustomerListResponse convertCustomerSingletonResponse(CustomerEntity entity) {
        return CustomerListResponse.builder()
                .data(Collections.singletonList(convertToCustomerResponse(entity)))
                .build();
    }

    public static CustomerEntity convertRequestToEntity(CustomerRequest request) {
        return CustomerEntity.builder()
                .documentType(DocumentType.valueOf(request.getDocumentType().toUpperCase()))
                .documentNumber(request.getDocumentNumber())
                .fullName(request.getFullName())
                .businessName(request.getBusinessName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .customerType(CustomerType.valueOf(request.getCustomerType().toUpperCase()))
                .createdAt(String.valueOf(System.currentTimeMillis()))
                .updatedAt(String.valueOf(System.currentTimeMillis()))
                .build();
    }


    public static CustomerResponse convertEntityToResponse(CustomerEntity entity) {
        return CustomerResponse.builder()
                .codResponse(200)
                .messageResponse("Cliente registrado exitosamente")
                .codEntity(entity.getId())
                .build();
    }



    public static Mono<CustomerListResponse> handleErrorCustomerMono(Throwable error) {
        return Mono.error(CustomerErrorFactory.createException(error));
    }

    public static Mono<CustomerResponse> handleErrorCustomerResponse(Throwable error) {
        return Mono.just(
                CustomerResponse.builder()
                        .codResponse(500)
                        .messageResponse("Error al procesar cliente: " + error.getMessage())
                        .codEntity(null)
                        .build()
        );
    }

    public static CustomerResponse convertCustomerResponseDelete(String idUser) {
        return CustomerResponse.builder()
                .codResponse(Constantes.COD_RESPONSE)
                .messageResponse(Constantes.CUSTOMER_DELETED)
                .build();
    }

}
