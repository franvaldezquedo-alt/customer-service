package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.error.CustomerErrorFactory;
import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
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

    public static Mono<CustomerListResponse> handleErrorCustomerMono(Throwable error) {
        return Mono.error(CustomerErrorFactory.createException(error));
    }
}
