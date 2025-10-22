package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.model.Customer;
import com.nttdata.customer_service.domain.model.CustomerListResponse;
import com.nttdata.customer_service.domain.model.CustomerType;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepositoryOutputPort customerRepositoryOutputPort;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testFindAllCustomers() {
        Customer customer1 = Customer.builder()
                .id("1")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .fullName("John Doe")
                .businessName("John's Business")
                .email("john123@gmail.com")
                .phoneNumber("555-1234")
                .address("123 Main St")
                .customerType(CustomerType.PERSONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .id("2")
                .documentType(DocumentType.PASSPORT)
                .documentNumber("A9876543")
                .fullName("Jane Smith")
                .businessName("Jane's Enterprises")
                .email("jane123@gmail.com")
                .phoneNumber("555-5678")
                .address("456 Elm St")
                .customerType(CustomerType.BUSINESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Customer customer3 = Customer.builder()
                .id("3")
                .documentType(DocumentType.DNI)
                .documentNumber("87654321")
                .fullName("Alice Johnson")
                .businessName("")
                .email("alice124@gmail.com")
                .phoneNumber("555-8765")
                .address("789 Oak St")
                .customerType(CustomerType.PERSONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Customer> customers = List.of(customer1, customer2, customer3);

        Mockito.when(customerRepositoryOutputPort.findAllCustomer())
                .thenReturn(Flux.fromIterable(customers));

        Mono <CustomerListResponse> responseMono = customerService.findAllCustomer();

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertNotNull(response);
                    assert response.getData().size() == 3;
                    assert response.getData().get(0).getId().equals("1");
                    assert response.getData().get(1).getId().equals("2");
                    assert response.getData().get(2).getId().equals("3");
                })
                .verifyComplete();

        Mockito.verify(customerRepositoryOutputPort, Mockito.times(1)).findAllCustomer();

    }

    @Test
    void errorFindAllCustomers() {

        RuntimeException error = new RuntimeException("Database connection error");

        Mockito.when(customerRepositoryOutputPort.findAllCustomer())
                .thenReturn(Flux.error(error));

        Mono<CustomerListResponse> responseMono = customerService.findAllCustomer();

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection error"))
                .verify();

        Mockito.verify(customerRepositoryOutputPort).findAllCustomer();
    }





}
