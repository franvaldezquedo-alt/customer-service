package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.error.CustomerNotFoundExeptions;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import com.nttdata.customer_service.infrastructure.utils.Constantes;
import com.nttdata.customer_service.infrastructure.utils.CustomerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerService implements CustomerInputPort {

    private final CustomerRepositoryOutputPort customerRepositoryOutputPort;

    public CustomerService(CustomerRepositoryOutputPort customerRepositoryOutputPort) {
        this.customerRepositoryOutputPort = customerRepositoryOutputPort;
    }

    @Override
    public Mono<CustomerListResponse> findAllCustomer() {
        return customerRepositoryOutputPort
                .findAllCustomer()
                .collectList()
                .map(CustomerUtils::convertCustomerListResponse)
                .doOnError(error -> log.error(error.getMessage()));
    }

    @Override
    public Mono<CustomerListResponse> findByIdCustomer(String id) {
        return customerRepositoryOutputPort
                .findByIdCustomer(id)
                .map(CustomerUtils::convertCustomerSingletonResponse)
                .switchIfEmpty(Mono.error(new CustomerNotFoundExeptions(Constantes.CUSTOMER_NOT_FOUND)))
                .doOnError(error -> log.error(error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerMono);

    }

    @Override
    public Mono<CustomerResponse> saveCustomer(CustomerRequest customerRequest) {
        log.info("Iniciando registro de nuevo cliente: {}", customerRequest);

        return Mono.just(customerRequest)
                // Convertimos la solicitud en entidad
                .map(CustomerUtils::convertRequestToEntity)
                // Guardamos en la base de datos (repositorio reactivo)
                .flatMap(customerRepositoryOutputPort::saveOrUpdateCustomer)
                // Convertimos la entidad guardada en respuesta
                .map(CustomerUtils::convertEntityToResponse)
                // Log de éxito
                .doOnSuccess(response -> log.info("Cliente registrado exitosamente: {}", response))
                // Manejo de errores
                .doOnError(error -> log.error("Error al registrar cliente: {}", error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> deleteByIdCustomer(String id) {
        return customerRepositoryOutputPort
                .findByIdCustomer(id)
                .flatMap(existingCustomer -> {
                      return customerRepositoryOutputPort
                        .deleteByIdCustomer(id)
                        .then(
                                Mono.defer(()-> Mono.just(CustomerUtils.convertCustomerResponseDelete(id))));
                })
                .switchIfEmpty(Mono.error(new CustomerNotFoundExeptions(Constantes.CUSTOMER_NOT_FOUND + id)))
                .doOnError(error -> log.error(Constantes.ERROR_DELETE + "{}", error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerResponse);

    }

}
