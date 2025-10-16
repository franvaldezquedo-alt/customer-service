package com.nttdata.customer_service.application.port.out;

import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import reactor.core.publisher.Flux;

public interface CustomerRepositoryOutputPort {
    Flux<CustomerEntity> findAllCustomer();
}
