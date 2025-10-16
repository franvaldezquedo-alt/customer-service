package com.nttdata.customer_service.infrastructure.controller;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
