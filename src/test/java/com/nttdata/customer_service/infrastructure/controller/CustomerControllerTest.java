package com.nttdata.customer_service.infrastructure.controller;

import com.nttdata.customer_service.application.port.in.CustomerInputPort;
import com.nttdata.customer_service.domain.model.*;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para CustomerController usando el patrón AAA (Arrange-Act-Assert).
 * <p>
 * Este test verifica el comportamiento de todos los endpoints del controlador
 * utilizando Mockito para simular las dependencias y StepVerifier para validar
 * las operaciones reactivas.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerController - Test Unitarios")
class CustomerControllerTest {

    @Mock
    private CustomerInputPort customerInputPort;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer;
    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;
    private CustomerListResponse customerListResponse;

    @BeforeEach
    void setUp() {
        // Datos de prueba comunes
        customer = Customer.builder()
                .id("1")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .fullName("Juan Pérez García")
                .businessName(null)
                .email("juan.perez@email.com")
                .phoneNumber("987654321")
                .address("Av. Principal 123")
                .customerType(CustomerType.PERSONAL)
                .status(StatusType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customerRequest = CustomerRequest.builder()
                .id("1")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .fullName("Juan Pérez García")
                .businessName(null)
                .email("juan.perez@email.com")
                .phoneNumber("987654321")
                .address("Av. Principal 123")
                .customerType(CustomerType.PERSONAL)
                .build();

        customerResponse = CustomerResponse.builder()
                .codResponse(200)
                .messageResponse("Operación exitosa")
                .codEntity("1")
                .build();

        customerListResponse = CustomerListResponse.builder()
                .data(Arrays.asList(customer))
                .error(null)
                .build();
    }

    @Test
    @DisplayName("Debe obtener todos los clientes exitosamente")
    void getAllCustomers_ShouldReturnAllCustomers_WhenCalled() {
        // Arrange
        when(customerInputPort.findAllCustomer())
                .thenReturn(Mono.just(customerListResponse));

        // Act
        Mono<CustomerListResponse> result = customerController.getAllCustomers();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() != null &&
                                response.getData().size() == 1 &&
                                response.getData().get(0).getId().equals("1") &&
                                response.getData().get(0).getFullName().equals("Juan Pérez García") &&
                                response.getError() == null
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findAllCustomer();
    }

    @Test
    @DisplayName("Debe obtener un cliente por ID exitosamente")
    void getCustomerById_ShouldReturnCustomer_WhenIdExists() {
        // Arrange
        String customerId = "1";
        when(customerInputPort.findByIdCustomer(customerId))
                .thenReturn(Mono.just(customerListResponse));

        // Act
        Mono<CustomerListResponse> result = customerController.getCustomerById(customerId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() != null &&
                                !response.getData().isEmpty() &&
                                response.getData().get(0).getId().equals(customerId) &&
                                response.getError() == null
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findByIdCustomer(customerId);
    }

    @Test
    @DisplayName("Debe crear un nuevo cliente exitosamente")
    void saveCustomer_ShouldReturnSavedCustomer_WhenValidRequest() {
        // Arrange
        when(customerInputPort.saveCustomer(any(CustomerRequest.class)))
                .thenReturn(Mono.just(customerResponse));

        // Act
        Mono<CustomerResponse> result = customerController.saveCustomer(customerRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getCodResponse() == 200 &&
                                response.getMessageResponse().equals("Operación exitosa") &&
                                response.getCodEntity() != null &&
                                response.getCodEntity().equals("1")
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).saveCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente existente exitosamente")
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenValidRequest() {
        // Arrange
        customerRequest.setFullName("Juan Carlos Pérez García");
        CustomerResponse updatedResponse = CustomerResponse.builder()
                .codResponse(200)
                .messageResponse("Cliente actualizado exitosamente")
                .codEntity("1")
                .build();

        when(customerInputPort.updateCustomer(any(CustomerRequest.class)))
                .thenReturn(Mono.just(updatedResponse));

        // Act
        Mono<CustomerResponse> result = customerController.updateCustomer(customerRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getCodResponse() == 200 &&
                                response.getMessageResponse().equals("Cliente actualizado exitosamente") &&
                                response.getCodEntity().equals("1")
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).updateCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Debe eliminar un cliente exitosamente")
    void deleteCustomer_ShouldReturnSuccessResponse_WhenIdExists() {
        // Arrange
        String customerId = "1";
        CustomerResponse deleteResponse = CustomerResponse.builder()
                .codResponse(200)
                .messageResponse("Cliente eliminado exitosamente")
                .codEntity(customerId)
                .build();

        when(customerInputPort.deleteByIdCustomer(customerId))
                .thenReturn(Mono.just(deleteResponse));

        // Act
        Mono<CustomerResponse> result = customerController.deleteCustomer(customerId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getCodResponse() == 200 &&
                                response.getMessageResponse().equals("Cliente eliminado exitosamente") &&
                                response.getCodEntity().equals(customerId)
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).deleteByIdCustomer(customerId);
    }

    @Test
    @DisplayName("Debe buscar cliente por tipo y número de documento exitosamente")
    void getCustomerByDocument_ShouldReturnCustomer_WhenDocumentExists() {
        // Arrange
        DocumentType documentType = DocumentType.DNI;
        String documentNumber = "12345678";

        when(customerInputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber))
                .thenReturn(Mono.just(customerListResponse));

        // Act
        Mono<CustomerListResponse> result = customerController.getCustomerByDocument(
                documentType, documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() != null &&
                                !response.getData().isEmpty() &&
                                response.getData().get(0).getDocumentType().equals(documentType) &&
                                response.getData().get(0).getDocumentNumber().equals(documentNumber) &&
                                response.getError() == null
                )
                .verifyComplete();

        verify(customerInputPort, times(1))
                .findByDocumentTypeAndDocumentNumber(documentType, documentNumber);
    }

    @Test
    @DisplayName("Debe buscar cliente por número de documento exitosamente")
    void getCustomerByDocumentNumber_ShouldReturnCustomer_WhenDocumentNumberExists() {
        // Arrange
        String documentNumber = "12345678";

        when(customerInputPort.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.just(customerListResponse));

        // Act
        Mono<CustomerListResponse> result = customerController
                .getCustomerByDocumentNumber(documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() != null &&
                                !response.getData().isEmpty() &&
                                response.getData().get(0).getDocumentNumber().equals(documentNumber) &&
                                response.getError() == null
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findByDocumentNumber(documentNumber);
    }

    @Test
    @DisplayName("Debe retornar respuesta vacía cuando no hay clientes")
    void getAllCustomers_ShouldReturnEmptyList_WhenNoCustomersExist() {
        // Arrange
        CustomerListResponse emptyResponse = CustomerListResponse.builder()
                .data(Collections.emptyList())
                .error(null)
                .build();

        when(customerInputPort.findAllCustomer())
                .thenReturn(Mono.just(emptyResponse));

        // Act
        Mono<CustomerListResponse> result = customerController.getAllCustomers();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() != null &&
                                response.getData().isEmpty() &&
                                response.getError() == null
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findAllCustomer();
    }

    @Test
    @DisplayName("Debe retornar error cuando no se encuentra cliente por ID")
    void getCustomerById_ShouldReturnError_WhenCustomerNotFound() {
        // Arrange
        String customerId = "999";
        CustomerListResponse errorResponse = CustomerListResponse.builder()
                .data(null)
                .error("Cliente no encontrado con ID: " + customerId)
                .build();

        when(customerInputPort.findByIdCustomer(customerId))
                .thenReturn(Mono.just(errorResponse));

        // Act
        Mono<CustomerListResponse> result = customerController.getCustomerById(customerId);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() == null &&
                                response.getError() != null &&
                                response.getError().contains("Cliente no encontrado")
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findByIdCustomer(customerId);
    }

    @Test
    @DisplayName("Debe manejar error cuando el servicio falla al buscar por ID")
    void getCustomerById_ShouldPropagateError_WhenServiceFails() {
        // Arrange
        String customerId = "999";
        when(customerInputPort.findByIdCustomer(customerId))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act
        Mono<CustomerListResponse> result = customerController.getCustomerById(customerId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Error de base de datos")
                )
                .verify();

        verify(customerInputPort, times(1)).findByIdCustomer(customerId);
    }

    @Test
    @DisplayName("Debe manejar error cuando el servicio falla al guardar")
    void saveCustomer_ShouldPropagateError_WhenServiceFails() {
        // Arrange
        when(customerInputPort.saveCustomer(any(CustomerRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Error al guardar cliente")));

        // Act
        Mono<CustomerResponse> result = customerController.saveCustomer(customerRequest);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Error al guardar cliente")
                )
                .verify();

        verify(customerInputPort, times(1)).saveCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Debe crear un cliente empresarial exitosamente")
    void saveCustomer_ShouldCreateBusinessCustomer_WhenValidBusinessRequest() {
        // Arrange
        CustomerRequest businessRequest = CustomerRequest.builder()
                .documentType(DocumentType.RUC)
                .documentNumber("20123456789")
                .fullName("Representante Legal")
                .businessName("Empresa SAC")
                .email("contacto@empresa.com")
                .phoneNumber("016549876")
                .address("Av. Empresarial 456")
                .customerType(CustomerType.BUSINESS)
                .build();

        CustomerResponse businessResponse = CustomerResponse.builder()
                .codResponse(200)
                .messageResponse("Cliente empresarial creado exitosamente")
                .codEntity("2")
                .build();

        when(customerInputPort.saveCustomer(any(CustomerRequest.class)))
                .thenReturn(Mono.just(businessResponse));

        // Act
        Mono<CustomerResponse> result = customerController.saveCustomer(businessRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getCodResponse() == 200 &&
                                response.getMessageResponse().contains("empresarial") &&
                                response.getCodEntity().equals("2")
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).saveCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Debe retornar error cuando no se encuentra cliente por documento")
    void getCustomerByDocumentNumber_ShouldReturnError_WhenDocumentNotFound() {
        // Arrange
        String documentNumber = "99999999";
        CustomerListResponse errorResponse = CustomerListResponse.builder()
                .data(null)
                .error("No se encontró cliente con número de documento: " + documentNumber)
                .build();

        when(customerInputPort.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.just(errorResponse));

        // Act
        Mono<CustomerListResponse> result = customerController
                .getCustomerByDocumentNumber(documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getData() == null &&
                                response.getError() != null &&
                                response.getError().contains("No se encontró cliente")
                )
                .verifyComplete();

        verify(customerInputPort, times(1)).findByDocumentNumber(documentNumber);
    }
}