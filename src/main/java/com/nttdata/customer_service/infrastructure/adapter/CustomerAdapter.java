package com.nttdata.customer_service.infrastructure.adapter;

import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CustomerAdapter implements CustomerRepositoryOutputPort {

    private final CustomerRepository repository;

    @Override
    public Flux<CustomerEntity> findAllCustomer() {
        return repository.findAll();
    }
}
