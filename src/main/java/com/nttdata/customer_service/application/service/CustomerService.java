package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.error.CustomerNotFoundExeptions;
import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.domain.model.StatusType;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import com.nttdata.customer_service.infrastructure.utils.Constants;
import com.nttdata.customer_service.infrastructure.utils.CustomerMapper;
import com.nttdata.customer_service.infrastructure.utils.CustomerResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerInputPort {

  private final CustomerRepositoryOutputPort customerRepositoryOutputPort;
  private final CustomerMapper customerMapper;
  private final CustomerResponseMapper customerResponseMapper;

  // ------------------------------------------------------------
  // LISTAR TODOS
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerListResponse> findAllCustomer() {
    log.debug("Consultando todos los clientes ACTIVOS");

    return customerRepositoryOutputPort.findAllCustomer()
          .filter(customer -> customer.getStatus() == StatusType.ACTIVE)
          .collectList()
          .map(customerResponseMapper::toCustomerListResponse)
          .doOnSuccess(response ->
                log.debug("Se encontraron {} clientes activos",
                      response.getData() != null ? response.getData().size() : 0))
          .doOnError(error ->
                log.error("Error al consultar clientes activos: {}", error.getMessage(), error));
  }

  // ------------------------------------------------------------
  // BUSCAR POR ID
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerListResponse> findByIdCustomer(String id) {
    log.debug("Buscando cliente con id: {}", id);

    return customerRepositoryOutputPort.findByIdCustomer(id)
          .map(customerResponseMapper::toSingletonResponse)
          .switchIfEmpty(Mono.defer(() -> {
            log.warn("Cliente no encontrado con id: {}", id);
            return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND));
          }))
          .doOnSuccess(response -> log.debug("Cliente encontrado con id: {}", id))
          .doOnError(error ->
                log.error("Error al buscar cliente con id {}: {}", id, error.getMessage(), error));
  }

  // ------------------------------------------------------------
  // CREAR NUEVO CLIENTE
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerResponse> saveCustomer(CustomerRequest customerRequest) {
    log.info("Iniciando registro de nuevo cliente: {}", customerRequest);

    return customerRepositoryOutputPort
          .findByDocumentTypeAndDocumentNumber(
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
            Customer newCustomer = customerMapper.fromRequest(customerRequest);
            return customerRepositoryOutputPort.saveOrUpdateCustomer(newCustomer)
                  .map(saved -> customerResponseMapper.toSuccessResponse(
                        saved.getId(), "Cliente registrado exitosamente"))
                  .doOnSuccess(response ->
                        log.info("Cliente registrado exitosamente con id: {}", response.getCodEntity()));
          }))
          .onErrorResume(e -> {
            log.error("Error al registrar cliente: {}", e.getMessage(), e);
            return Mono.just(customerResponseMapper.toErrorResponse(e.getMessage()));
          });
  }

  // ------------------------------------------------------------
  // ACTUALIZAR CLIENTE
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerResponse> updateCustomer(CustomerRequest customerRequest) {
    log.info("Actualizando cliente con id: {}", customerRequest.getId());

    return customerRepositoryOutputPort.findByIdCustomer(customerRequest.getId())
          .switchIfEmpty(Mono.defer(() -> {
            log.warn("No se puede actualizar. Cliente no encontrado con id: {}", customerRequest.getId());
            return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND));
          }))
          .flatMap(existingCustomer -> {
            Customer updated = customerMapper.updateFromRequest(existingCustomer, customerRequest);
            return customerRepositoryOutputPort.saveOrUpdateCustomer(updated);
          })
          .map(saved -> customerResponseMapper.toSuccessResponse(
                saved.getId(), "Cliente actualizado exitosamente"))
          .doOnSuccess(response ->
                log.info("Cliente actualizado exitosamente con id: {}", response.getCodEntity()))
          .onErrorResume(e -> {
            log.error("Error al actualizar cliente {}: {}", customerRequest.getId(), e.getMessage(), e);
            return Mono.just(customerResponseMapper.toErrorResponse(e.getMessage()));
          });
  }

  // ------------------------------------------------------------
  // ELIMINAR CLIENTE
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerResponse> deleteByIdCustomer(String id) {
    log.info("Desactivando cliente con id: {}", id);

    return customerRepositoryOutputPort.findByIdCustomer(id)
          .switchIfEmpty(Mono.defer(() -> {
            log.warn("Cliente no encontrado con id: {}", id);
            return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND));
          }))
          .flatMap(customer -> {
            // Si ya está inactivo, devolver respuesta de error
            if (StatusType.INACTIVE.equals(customer.getStatus())) {
              log.warn("El cliente con id {} ya está inactivo.", id);
              return Mono.just(customerResponseMapper
                    .toErrorResponse("Customer is already inactive"));
            }

            // Cambiar estado y fecha
            customer.setStatus(StatusType.INACTIVE);
            customer.setUpdatedAt(LocalDateTime.now());

            // Guardar cliente actualizado
            return customerRepositoryOutputPort.saveOrUpdateCustomer(customer)
                  .map(saved -> customerResponseMapper
                        .toSuccessResponse(saved.getId(), "Customer successfully deactivated"));
          })
          .doOnSuccess(response -> log.info("Cliente desactivado correctamente con id: {}", id))
          .onErrorResume(e -> {
            log.error("Error al desactivar cliente {}: {}", id, e.getMessage(), e);
            return Mono.just(customerResponseMapper
                  .toErrorResponse("Error deleting customer: " + e.getMessage()));
          });
  }

  // ------------------------------------------------------------
  // BUSCAR POR DOCUMENTO
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerListResponse> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
    log.debug("Buscando cliente con tipo: {} y número: {}", documentType, documentNumber);

    if (documentType == null || documentNumber == null || documentNumber.isBlank()) {
      log.warn("Tipo o número de documento inválido: {} - {}", documentType, documentNumber);
      return Mono.error(new IllegalArgumentException("El tipo y número de documento son obligatorios"));
    }

    return customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
          .filter(Objects::nonNull)
          .map(customerResponseMapper::toSingletonResponse)
          .switchIfEmpty(Mono.defer(() -> {
            log.warn("Cliente no encontrado con tipo: {} y número: {}", documentType, documentNumber);
            return Mono.error(new CustomerNotFoundExeptions(Constants.CUSTOMER_NOT_FOUND));
          }))
          .doOnError(error ->
                log.error("Error al buscar cliente por documento: {}", error.getMessage(), error));
  }
}
