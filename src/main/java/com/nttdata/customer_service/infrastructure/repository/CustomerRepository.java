package com.nttdata.customer_service.infrastructure.repository;

import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {
}
