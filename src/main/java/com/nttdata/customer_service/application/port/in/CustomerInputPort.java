package com.nttdata.customer_service.application.port.in;


import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import reactor.core.publisher.Mono;

public interface CustomerInputPort {
    Mono<CustomerListResponse> findAllCustomer();
    Mono<CustomerListResponse> findByIdCustomer(String id);
    Mono<CustomerResponse> saveCustomer(CustomerRequest customerListResponseMono);
    Mono<CustomerResponse> deleteByIdCustomer(String id);
    Mono<CustomerResponse> updateCustomer(CustomerRequest customerRequest);
    Mono<CustomerListResponse> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
    Mono<CustomerListResponse> findByDocumentNumber(String documentNumber);
}
