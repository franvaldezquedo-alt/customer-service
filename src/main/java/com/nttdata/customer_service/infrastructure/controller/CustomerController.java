package com.nttdata.customer_service.infrastructure.controller;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerResponse;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
public class CustomerController {

    private final CustomerInputPort customerInputPort;

    public CustomerController(CustomerInputPort customerInputPort) {
        this.customerInputPort = customerInputPort;
    }

    @GetMapping("/all")
   Mono<CustomerListResponse> getAllCustomers() {
        return customerInputPort.findAllCustomer();
    }

    @GetMapping("/{id}")
    Mono<CustomerListResponse> getCustomerById(@PathVariable  String id) {
        return customerInputPort.findByIdCustomer(id);
    }

    @PostMapping("/save")
    Mono<CustomerResponse> saveCustomer(@Valid  @RequestBody CustomerRequest customerRequest) {
        return customerInputPort.saveCustomer(customerRequest);
    }
}
