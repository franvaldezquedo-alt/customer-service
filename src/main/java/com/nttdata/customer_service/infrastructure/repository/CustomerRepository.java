package com.nttdata.customer_service.infrastructure.repository;

import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<CustomerEntity, String> {
    Mono<CustomerEntity> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
    Mono<CustomerEntity> findByDocumentNumber(String documentNumber);
}
