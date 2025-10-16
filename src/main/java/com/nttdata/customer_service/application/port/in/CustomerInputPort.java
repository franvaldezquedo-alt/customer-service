package com.nttdata.customer_service.application.port.in;


import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerInputPort {
    Mono<CustomerListResponse> findAllCustomer();
    Mono<CustomerListResponse> findByIdCustomer(String id);
    //Mono<CustomerResponse> saveCustomer(CustomerRequest customerListResponseMono);
}
