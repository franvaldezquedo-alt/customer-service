package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.error.CustomerNotFoundExeptions;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import com.nttdata.customer_service.infrastructure.utils.Constants;
import com.nttdata.customer_service.infrastructure.utils.CustomerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerInputPort {

    private final CustomerRepositoryOutputPort customerRepositoryOutputPort;

    @Override
    public Mono<CustomerListResponse> findAllCustomer() {
        log.debug("Consultando todos los clientes");

        return customerRepositoryOutputPort.findAllCustomer()
                .collectList()
                .map(CustomerUtils::convertCustomerListResponse)
                .doOnSuccess(response -> log.debug("Se encontraron {} clientes",
                        response.getData() != null ? response.getData().size() : 0))
                .doOnError(error -> log.error("Error al consultar clientes: {}", error.getMessage()));
    }

    @Override
    public Mono<CustomerListResponse> findByIdCustomer(String id) {
        log.debug("Buscando cliente con id: {}", id);

        return customerRepositoryOutputPort.findByIdCustomer(id)
                .map(CustomerUtils::convertCustomerSingletonResponse)
                .doOnSuccess(response -> log.debug("Cliente encontrado: {}", id))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Cliente no encontrado con id: {}", id);
                    return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND));
                }))
                .onErrorResume(CustomerUtils::handleErrorCustomerMono);
    }

    @Override
    public Mono<CustomerResponse> saveCustomer(CustomerRequest customerRequest) {
        log.info("Iniciando registro de nuevo cliente: {}", customerRequest);

        return customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
                        customerRequest.getDocumentType(),
                        customerRequest.getDocumentNumber())
                .flatMap(existingCustomer -> {
                    log.warn("Cliente ya existe con documento {} {}",
                            customerRequest.getDocumentType(),
                            customerRequest.getDocumentNumber());
                    return Mono.<CustomerResponse>error(new IllegalArgumentException(Constants.CUSTOMER_ALREADY_EXISTS));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Documento no existe, procediendo a crear cliente");
                    return Mono.just(customerRequest)
                            .map(CustomerUtils::convertRequestToEntity)
                            .flatMap(customerRepositoryOutputPort::saveOrUpdateCustomer)
                            .map(CustomerUtils::convertEntityToResponse)
                            .doOnSuccess(response -> log.info("Cliente registrado exitosamente con id: {}",
                                    response.getCodEntity()));
                }))
                .doOnError(error -> log.error("Error al registrar cliente: {}", error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> deleteByIdCustomer(String id) {
        log.info("Eliminando cliente con id: {}", id);

        return customerRepositoryOutputPort.findByIdCustomer(id)
                .flatMap(existingCustomer -> deleteCustomer(id))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No se puede eliminar. Cliente no encontrado con id: {}", id);
                    return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND + id));
                }))
                .doOnError(error -> log.error("Error al eliminar cliente {}: {}", id, error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerResponse);
    }

    @Override
    public Mono<CustomerResponse> updateCustomer(CustomerRequest customerRequest) {
        log.info("Actualizando cliente con id: {}", customerRequest.getId());

        return customerRepositoryOutputPort.findByIdCustomer(customerRequest.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No se puede actualizar. Cliente no encontrado con id: {}", customerRequest.getId());
                    return Mono.error(new CustomerNotFoundExeptions(
                            Constants.CUSTOMER_NOT_FOUND + customerRequest.getId()));
                }))
                .flatMap(existingCustomer -> {
                    CustomerEntity updatedEntity = CustomerUtils.convertCustomerUpdateEntity(customerRequest);
                    // Preservar la fecha de creación original
                    updatedEntity.setCreatedAt(existingCustomer.getCreatedAt());
                    return customerRepositoryOutputPort.saveOrUpdateCustomer(updatedEntity);
                })
                .map(CustomerUtils::convertCustomerResponseUpdate)
                .doOnSuccess(response -> log.info("Cliente actualizado exitosamente con id: {}", response.getCodEntity()))
                .doOnError(error -> log.error("Error al actualizar cliente {}: {}",
                        customerRequest.getId(), error.getMessage()))
                .onErrorResume(CustomerUtils::handleErrorCustomerResponse);
    }

    private Mono<CustomerResponse> deleteCustomer(String id) {
        return customerRepositoryOutputPort.deleteByIdCustomer(id)
                .then(Mono.fromCallable(() -> CustomerUtils.convertCustomerResponseDelete(id)))
                .doOnSuccess(response -> log.info("Cliente eliminado exitosamente con id: {}", id));
    }
}