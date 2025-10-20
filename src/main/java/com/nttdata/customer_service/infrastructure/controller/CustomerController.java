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

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
@Tag(name = "Customer", description = "Operaciones CRUD para clientes del banco")
public class CustomerController {

    private final CustomerInputPort customerInputPort;

    public CustomerController(CustomerInputPort customerInputPort) {
        this.customerInputPort = customerInputPort;
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todos los clientes")
   Mono<CustomerListResponse> getAllCustomers() {
        return customerInputPort.findAllCustomer();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por ID")
    Mono<CustomerListResponse> getCustomerById(@PathVariable  String id) {
        return customerInputPort.findByIdCustomer(id);
    }

    @PostMapping("/save")
    @Operation(summary = "Crear un nuevo cliente")
    Mono<CustomerResponse> saveCustomer(@Valid  @RequestBody CustomerRequest customerRequest) {
        return customerInputPort.saveCustomer(customerRequest);
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar un cliente existente")
    Mono<CustomerResponse> updateCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customerInputPort.updateCustomer(customerRequest);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un cliente")
    Mono<CustomerResponse> deleteCustomer(@PathVariable String id) {
        return customerInputPort.deleteByIdCustomer(id);
    }

    @GetMapping("/document")
    @Operation(summary = "Obtener un cliente por tipo y número de documento")
    public Mono<CustomerListResponse> getCustomerByDocument(
            @RequestParam DocumentType documentType,
            @RequestParam String documentNumber) {

        return customerInputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber);
    }


}
