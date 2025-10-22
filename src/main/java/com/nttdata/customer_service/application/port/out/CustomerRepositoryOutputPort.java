package com.nttdata.customer_service.application.port.out;

import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.DocumentType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepositoryOutputPort {
    Flux<Customer> findAllCustomer();
    Mono<Customer> findByIdCustomer(String idCustomer);
    Mono<Customer> saveOrUpdateCustomer(Customer customer);
    Mono<Void> deleteByIdCustomer(String idCustomer);
    Mono<Customer> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
}

