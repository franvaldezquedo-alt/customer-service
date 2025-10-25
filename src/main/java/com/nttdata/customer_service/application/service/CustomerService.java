package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.error.*;
import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.domain.model.StatusType;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
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
    return Mono.justOrEmpty(id)
          .filter(value -> !value.trim().isEmpty())
          .switchIfEmpty(Mono.error(new EmptyCustomerIdException("El ID del cliente no puede estar vacío")))
          .flatMap(validId ->
                customerRepositoryOutputPort.findByIdCustomer(validId)
                      .map(customerResponseMapper::toSingletonResponse)
                      .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                            "Cliente no encontrado con id: " + validId)))
                      .doOnSuccess(response -> log.debug("Cliente encontrado con id: {}", validId))
          )
          .doOnSubscribe(sub -> log.debug("Buscando cliente con id: {}", id));
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
            return Mono.<Customer>error(new CustomerAlreadyExistsException(
                  customerRequest.getDocumentType().toString(),
                  customerRequest.getDocumentNumber()));
          })
          .switchIfEmpty(Mono.defer(() -> {
            log.debug("Documento no existe, procediendo a crear cliente");
            Customer newCustomer = customerMapper.fromRequest(customerRequest);
            return customerRepositoryOutputPort.saveOrUpdateCustomer(newCustomer);
          }))
          .map(saved -> customerResponseMapper.toSuccessResponse(
                saved.getId(), "Cliente registrado exitosamente"))
          .doOnSuccess(response ->
                log.info("Cliente registrado exitosamente con id: {}", response.getCodEntity()))
          .onErrorResume(e -> {
            if (e instanceof CustomerAlreadyExistsException) {
              log.error("Error: {}", e.getMessage());
              return Mono.error(e); // Propagar la excepción al GlobalExceptionHandler
            }
            log.error("Error inesperado al registrar cliente: {}", e.getMessage(), e);
            return Mono.error(new CustomerServiceException("Error al registrar cliente", e));
          });
  }

  // ------------------------------------------------------------
  // ACTUALIZAR CLIENTE
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerResponse> updateCustomer(CustomerRequest customerRequest) {
    log.info("Actualizando cliente con id: {}", customerRequest.getId());

    return Mono.justOrEmpty(customerRequest.getId())
          .filter(id -> !id.trim().isEmpty())
          .switchIfEmpty(Mono.error(new EmptyCustomerIdException("El ID del cliente es obligatorio para actualizar")))
          .flatMap(validId ->
                customerRepositoryOutputPort.findByIdCustomer(validId)
                      .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                            "No se puede actualizar. Cliente no encontrado con id: " + validId)))
                      .flatMap(existingCustomer -> {
                        Customer updated = customerMapper.updateFromRequest(existingCustomer, customerRequest);
                        return customerRepositoryOutputPort.saveOrUpdateCustomer(updated);
                      })
                      .map(saved -> customerResponseMapper.toSuccessResponse(
                            saved.getId(), "Cliente actualizado exitosamente"))
                      .doOnSuccess(response ->
                            log.info("Cliente actualizado exitosamente con id: {}", response.getCodEntity()))
          )
          .onErrorResume(e -> {
            if (e instanceof CustomerNotFoundException || e instanceof EmptyCustomerIdException) {
              return Mono.error(e);
            }
            log.error("Error inesperado al actualizar cliente: {}", e.getMessage(), e);
            return Mono.error(new CustomerServiceException("Error al actualizar cliente", e));
          });
  }

  // ------------------------------------------------------------
  // ELIMINAR CLIENTE (Desactivar)
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerResponse> deleteByIdCustomer(String id) {
    log.info("Desactivando cliente con id: {}", id);

    return Mono.justOrEmpty(id)
          .filter(value -> !value.trim().isEmpty())
          .switchIfEmpty(Mono.error(new EmptyCustomerIdException("El ID del cliente es obligatorio para eliminar")))
          .flatMap(validId ->
                customerRepositoryOutputPort.findByIdCustomer(validId)
                      .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                            "Cliente no encontrado con id: " + validId)))
                      .flatMap(customer -> {
                        // Si ya está inactivo, lanzar excepción
                        if (StatusType.INACTIVE.equals(customer.getStatus())) {
                          log.warn("El cliente con id {} ya está inactivo.", validId);
                          return Mono.error(new CustomerAlreadyInactiveException(validId));
                        }

                        // Cambiar estado y fecha
                        customer.setStatus(StatusType.INACTIVE);
                        customer.setUpdatedAt(LocalDateTime.now());

                        // Guardar cliente actualizado
                        return customerRepositoryOutputPort.saveOrUpdateCustomer(customer);
                      })
                      .map(saved -> customerResponseMapper
                            .toSuccessResponse(saved.getId(), "Cliente desactivado exitosamente"))
                      .doOnSuccess(response -> log.info("Cliente desactivado correctamente con id: {}", validId))
          )
          .onErrorResume(e -> {
            if (e instanceof CustomerNotFoundException ||
                  e instanceof EmptyCustomerIdException ||
                  e instanceof CustomerAlreadyInactiveException) {
              return Mono.error(e);
            }
            log.error("Error inesperado al desactivar cliente: {}", e.getMessage(), e);
            return Mono.error(new CustomerServiceException("Error al desactivar cliente", e));
          });
  }

  // ------------------------------------------------------------
  // BUSCAR POR TIPO Y NÚMERO DE DOCUMENTO
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerListResponse> findByDocumentTypeAndDocumentNumber(
        DocumentType documentType, String documentNumber) {
    log.debug("Buscando cliente con tipo: {} y número: {}", documentType, documentNumber);

    if (documentType == null || documentNumber == null || documentNumber.isBlank()) {
      log.warn("Tipo o número de documento inválido: {} - {}", documentType, documentNumber);
      return Mono.error(new InvalidDocumentException("El tipo y número de documento son obligatorios"));
    }

    return customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
          .filter(Objects::nonNull)
          .map(customerResponseMapper::toSingletonResponse)
          .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                String.format("Cliente no encontrado con tipo: %s y número: %s", documentType, documentNumber))))
          .doOnError(error ->
                log.error("Error al buscar cliente por documento: {}", error.getMessage(), error));
  }

  // ------------------------------------------------------------
  // BUSCAR POR NÚMERO DE DOCUMENTO
  // ------------------------------------------------------------
  @Override
  public Mono<CustomerListResponse> findByDocumentNumber(String documentNumber) {
    log.debug("Buscando cliente con número: {}", documentNumber);

    if (documentNumber == null || documentNumber.isBlank()) {
      log.warn("El número de documento inválido: {}", documentNumber);
      return Mono.error(new InvalidDocumentException("El número de documento es obligatorio"));
    }

    return customerRepositoryOutputPort.findByDocumentNumber(documentNumber)
          .filter(Objects::nonNull)
          .map(customerResponseMapper::toSingletonResponse)
          .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                "Cliente no encontrado con número de documento: " + documentNumber)))
          .doOnError(error ->
                log.error("Error al buscar cliente por documento: {}", error.getMessage(), error));
  }
}