package com.nttdata.customer_service.infrastructure.controller;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * Controlador REST para la gestión de clientes del banco.
 * <p>
 * Este controlador expone endpoints reactivos para realizar operaciones CRUD
 * sobre los clientes, así como búsquedas específicas por documento.
 * </p>
 *
 * @author NTT Data
 * @version 1.0
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin
@Tag(name = "Customer", description = "Operaciones CRUD para clientes del banco")
public class CustomerController {

  private final CustomerInputPort customerInputPort;

  /**
   * Constructor para inyección de dependencias.
   *
   * @param customerInputPort puerto de entrada para operaciones de clientes
   */
  public CustomerController(CustomerInputPort customerInputPort) {
    this.customerInputPort = customerInputPort;
  }

  /**
   * Obtiene la lista completa de todos los clientes registrados en el sistema.
   *
   * @return Mono con la respuesta que contiene la lista de clientes
   */
  @GetMapping("/all")
  @Operation(summary = "Listar todos los clientes")
  Mono<CustomerListResponse> getAllCustomers() {
    return customerInputPort.findAllCustomer();
  }

  /**
   * Busca y retorna un cliente específico por su identificador único.
   *
   * @param id identificador único del cliente
   * @return Mono con la respuesta que contiene el cliente encontrado
   */
  @GetMapping("/{id}")
  @Operation(summary = "Obtener un cliente por ID")
  Mono<CustomerListResponse> getCustomerById(@PathVariable String id) {
    return customerInputPort.findByIdCustomer(id);
  }

  /**
   * Crea y guarda un nuevo cliente en el sistema.
   *
   * @param customerRequest objeto con los datos del nuevo cliente a crear
   * @return Mono con la respuesta de la operación de guardado
   */
  @PostMapping("/save")
  @Operation(summary = "Crear un nuevo cliente")
  Mono<CustomerResponse> saveCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
    return customerInputPort.saveCustomer(customerRequest);
  }

  /**
   * Actualiza la información de un cliente existente en el sistema.
   *
   * @param customerRequest objeto con los datos actualizados del cliente
   * @return Mono con la respuesta de la operación de actualización
   */
  @PutMapping("/update")
  @Operation(summary = "Actualizar un cliente existente")
  Mono<CustomerResponse> updateCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
    return customerInputPort.updateCustomer(customerRequest);
  }

  /**
   * Elimina un cliente del sistema por su identificador.
   *
   * @param id identificador único del cliente a eliminar
   * @return Mono con la respuesta de la operación de eliminación
   */
  @DeleteMapping("/delete/{id}")
  @Operation(summary = "Eliminar un cliente")
  Mono<CustomerResponse> deleteCustomer(@PathVariable String id) {
    return customerInputPort.deleteByIdCustomer(id);
  }

  /**
   * Busca un cliente por tipo y número de documento.
   * <p>
   * Permite realizar búsquedas específicas combinando el tipo de documento
   * (DNI, RUC, etc.) con el número correspondiente.
   * </p>
   *
   * @param documentType tipo de documento del cliente
   * @param documentNumber número de documento del cliente
   * @return Mono con la respuesta que contiene el cliente encontrado
   */
  @GetMapping("/document")
  @Operation(summary = "Obtener un cliente por tipo y número de documento")
  public Mono<CustomerListResponse> getCustomerByDocument(
        @RequestParam DocumentType documentType,
        @RequestParam String documentNumber) {

    return customerInputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber);
  }

  /**
   * Busca un cliente únicamente por su número de documento.
   * <p>
   * Realiza la búsqueda sin especificar el tipo de documento,
   * útil cuando solo se conoce el número.
   * </p>
   *
   * @param documentNumber número de documento del cliente
   * @return Mono con la respuesta que contiene el cliente encontrado
   */
  @GetMapping("/document/{documentNumber}")
  @Operation(summary = "Obtener un cliente por número de documento")
  public Mono<CustomerListResponse> getCustomerByDocumentNumber(@PathVariable String documentNumber) {
    return customerInputPort.findByDocumentNumber(documentNumber);
  }

}