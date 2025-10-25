package com.nttdata.customer_service.infrastructure.adapter;

import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.repository.CustomerRepository;
import com.nttdata.customer_service.infrastructure.utils.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerAdapter implements CustomerRepositoryOutputPort {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public Flux<Customer> findAllCustomer() {
        return repository.findAll()
              .map(mapper::toDomain);
    }

    @Override
    public Mono<Customer> findByIdCustomer(String idCustomer) {
        return repository.findById(idCustomer)
              .map(mapper::toDomain);
    }

    @Override
    public Mono<Customer> saveOrUpdateCustomer(Customer customer) {
      CustomerEntity entity = mapper.toEntity(customer);
        return repository.save(entity)
              .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdCustomer(String idCustomer) {
        return repository.deleteById(idCustomer);
    }

    @Override
    public Mono<Customer> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return repository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
              .map(mapper::toDomain);
    }

  @Override
  public Mono<Customer> findByDocumentNumber(String documentNumber) {
    return repository.findByDocumentNumber(documentNumber)
          .map(mapper::toDomain);
  }
}
