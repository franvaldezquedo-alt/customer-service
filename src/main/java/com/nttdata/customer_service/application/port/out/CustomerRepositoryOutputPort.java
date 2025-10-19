package com.nttdata.customer_service.application.port.out;

import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepositoryOutputPort {
    Flux<CustomerEntity> findAllCustomer();
    Mono<CustomerEntity> findByIdCustomer(String idCustomer);
    Mono<CustomerEntity> saveOrUpdateCustomer(CustomerEntity customerEntity);
    Mono<Void> deleteByIdCustomer(String idCustomer);
}

