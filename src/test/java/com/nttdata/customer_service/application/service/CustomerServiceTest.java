package com.nttdata.customer_service.application.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryOutputPort customerRepositoryOutputPort;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerResponseMapper customerResponseMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer activeCustomer;
    private Customer inactiveCustomer;
    private CustomerRequest customerRequest;
    private CustomerResponse customerResponse;
    private CustomerListResponse customerListResponse;

    @BeforeEach
    void setUp() {
        String customerId = UUID.randomUUID().toString();

        activeCustomer = Customer.builder()
                .id(customerId)
                .fullName("Juan Perez")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .email("juan.perez@example.com")
                .phoneNumber("+51987654321")
                .status(StatusType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inactiveCustomer = Customer.builder()
                .id(customerId)
                .fullName("Juan Perez")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .email("juan.perez@example.com")
                .phoneNumber("+51987654321")
                .status(StatusType.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customerRequest = CustomerRequest.builder()
                .id(customerId)
                .fullName("Juan Perz")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .email("juan.perez@example.com")
                .phoneNumber("+51987654321")
                .build();

        customerResponse = CustomerResponse.builder()
                .codEntity(customerId)
                .messageResponse("Operación exitosa")
                .build();

        customerListResponse = CustomerListResponse.builder()
                .data(Arrays.asList(activeCustomer))
                .build();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA LISTAR TODOS LOS CLIENTES
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería listar todos los clientes activos exitosamente")
    void findAllCustomer_ShouldReturnActiveCustomers() {
        // Arrange
        List<Customer> activeCustomers = Arrays.asList(activeCustomer);
        when(customerRepositoryOutputPort.findAllCustomer()).thenReturn(Flux.fromIterable(activeCustomers));
        when(customerResponseMapper.toCustomerListResponse(activeCustomers)).thenReturn(customerListResponse);

        // Act & Assert
        StepVerifier.create(customerService.findAllCustomer())
                .expectNext(customerListResponse)
                .verifyComplete();

        verify(customerRepositoryOutputPort).findAllCustomer();
        verify(customerResponseMapper).toCustomerListResponse(activeCustomers);
    }

    @Test
    @DisplayName("Debería filtrar solo clientes activos")
    void findAllCustomer_ShouldFilterOnlyActiveCustomers() {
        // Arrange
        List<Customer> mixedCustomers = Arrays.asList(activeCustomer, inactiveCustomer);
        List<Customer> activeCustomersOnly = Arrays.asList(activeCustomer);

        when(customerRepositoryOutputPort.findAllCustomer()).thenReturn(Flux.fromIterable(mixedCustomers));
        when(customerResponseMapper.toCustomerListResponse(activeCustomersOnly)).thenReturn(customerListResponse);

        // Act & Assert
        StepVerifier.create(customerService.findAllCustomer())
                .expectNext(customerListResponse)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no hay clientes activos")
    void findAllCustomer_ShouldReturnEmptyListWhenNoActiveCustomers() {
        // Arrange
        List<Customer> inactiveCustomers = Arrays.asList(inactiveCustomer);
        CustomerListResponse emptyResponse = CustomerListResponse.builder().data(Arrays.asList()).build();

        when(customerRepositoryOutputPort.findAllCustomer()).thenReturn(Flux.fromIterable(inactiveCustomers));
        when(customerResponseMapper.toCustomerListResponse(Arrays.asList())).thenReturn(emptyResponse);

        // Act & Assert
        StepVerifier.create(customerService.findAllCustomer())
                .expectNext(emptyResponse)
                .verifyComplete();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA BUSCAR POR ID
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería encontrar cliente por ID exitosamente")
    void findByIdCustomer_ShouldReturnCustomer() {
        // Arrange
        String customerId = activeCustomer.getId();
        when(customerRepositoryOutputPort.findByIdCustomer(customerId)).thenReturn(Mono.just(activeCustomer));
        when(customerResponseMapper.toSingletonResponse(activeCustomer)).thenReturn(customerListResponse);

        // Act & Assert
        StepVerifier.create(customerService.findByIdCustomer(customerId))
                .expectNext(customerListResponse)
                .verifyComplete();

        verify(customerRepositoryOutputPort).findByIdCustomer(customerId);
    }

    @Test
    @DisplayName("Debería lanzar EmptyCustomerIdException cuando ID es vacío")
    void findByIdCustomer_WithEmptyId_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByIdCustomer(""))
                .expectError(EmptyCustomerIdException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
    }

    @Test
    @DisplayName("Debería lanzar EmptyCustomerIdException cuando ID es null")
    void findByIdCustomer_WithNullId_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByIdCustomer(null))
                .expectError(EmptyCustomerIdException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
    }

    @Test
    @DisplayName("Debería lanzar CustomerNotFoundException cuando cliente no existe")
    void findByIdCustomer_WithNonExistentId_ShouldThrowException() {
        // Arrange
        String nonExistentId = "non-existent-id";
        when(customerRepositoryOutputPort.findByIdCustomer(nonExistentId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(customerService.findByIdCustomer(nonExistentId))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA CREAR CLIENTE
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería crear cliente exitosamente cuando no existe")
    void saveCustomer_ShouldCreateCustomerSuccessfully() {
        // Arrange
        when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
                customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
                .thenReturn(Mono.empty());
        when(customerMapper.fromRequest(customerRequest)).thenReturn(activeCustomer);
        when(customerRepositoryOutputPort.saveOrUpdateCustomer(activeCustomer)).thenReturn(Mono.just(activeCustomer));
        when(customerResponseMapper.toSuccessResponse(activeCustomer.getId(), "Cliente registrado exitosamente"))
                .thenReturn(customerResponse);

        // Act & Assert
        StepVerifier.create(customerService.saveCustomer(customerRequest))
                .expectNext(customerResponse)
                .verifyComplete();

        verify(customerRepositoryOutputPort).findByDocumentTypeAndDocumentNumber(
                customerRequest.getDocumentType(), customerRequest.getDocumentNumber());
        verify(customerRepositoryOutputPort).saveOrUpdateCustomer(activeCustomer);
    }

    @Test
    @DisplayName("Debería lanzar CustomerAlreadyExistsException cuando cliente ya existe")
    void saveCustomer_WhenCustomerExists_ShouldThrowException() {
        // Arrange
        when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
                customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
                .thenReturn(Mono.just(activeCustomer));

        // Act & Assert
        StepVerifier.create(customerService.saveCustomer(customerRequest))
                .expectError(CustomerAlreadyExistsException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).saveOrUpdateCustomer(any());
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA ACTUALIZAR CLIENTE
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería actualizar cliente exitosamente")
    void updateCustomer_ShouldUpdateCustomerSuccessfully() {
        // Arrange
        when(customerRepositoryOutputPort.findByIdCustomer(customerRequest.getId()))
                .thenReturn(Mono.just(activeCustomer));
        when(customerMapper.updateFromRequest(activeCustomer, customerRequest)).thenReturn(activeCustomer);
        when(customerRepositoryOutputPort.saveOrUpdateCustomer(activeCustomer)).thenReturn(Mono.just(activeCustomer));
        when(customerResponseMapper.toSuccessResponse(activeCustomer.getId(), "Cliente actualizado exitosamente"))
                .thenReturn(customerResponse);

        // Act & Assert
        StepVerifier.create(customerService.updateCustomer(customerRequest))
                .expectNext(customerResponse)
                .verifyComplete();

        verify(customerRepositoryOutputPort).findByIdCustomer(customerRequest.getId());
        verify(customerRepositoryOutputPort).saveOrUpdateCustomer(activeCustomer);
    }

    @Test
    @DisplayName("Debería lanzar EmptyCustomerIdException cuando ID es vacío en actualización")
    void updateCustomer_WithEmptyId_ShouldThrowException() {
        // Arrange
        CustomerRequest requestWithoutId = CustomerRequest.builder()
                .fullName("Juan Perez")
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .build();

        // Act & Assert
        StepVerifier.create(customerService.updateCustomer(requestWithoutId))
                .expectError(EmptyCustomerIdException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
    }

    @Test
    @DisplayName("Debería lanzar CustomerNotFoundException cuando cliente no existe para actualizar")
    void updateCustomer_WithNonExistentId_ShouldThrowException() {
        // Arrange
        when(customerRepositoryOutputPort.findByIdCustomer(customerRequest.getId()))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(customerService.updateCustomer(customerRequest))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA ELIMINAR CLIENTE
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería desactivar cliente exitosamente")
    void deleteByIdCustomer_ShouldDeactivateCustomerSuccessfully() {
        // Arrange
        String customerId = activeCustomer.getId();
        when(customerRepositoryOutputPort.findByIdCustomer(customerId))
                .thenReturn(Mono.just(activeCustomer));
        when(customerRepositoryOutputPort.saveOrUpdateCustomer(any(Customer.class)))
                .thenReturn(Mono.just(inactiveCustomer));
        when(customerResponseMapper.toSuccessResponse(customerId, "Cliente desactivado exitosamente"))
                .thenReturn(customerResponse);

        // Act & Assert
        StepVerifier.create(customerService.deleteByIdCustomer(customerId))
                .expectNext(customerResponse)
                .verifyComplete();

        verify(customerRepositoryOutputPort).saveOrUpdateCustomer(argThat(customer ->
                customer.getStatus() == StatusType.INACTIVE));
    }

    @Test
    @DisplayName("Debería lanzar CustomerAlreadyInactiveException cuando cliente ya está inactivo")
    void deleteByIdCustomer_WhenCustomerAlreadyInactive_ShouldThrowException() {
        // Arrange
        String customerId = inactiveCustomer.getId();
        when(customerRepositoryOutputPort.findByIdCustomer(customerId))
                .thenReturn(Mono.just(inactiveCustomer));

        // Act & Assert
        StepVerifier.create(customerService.deleteByIdCustomer(customerId))
                .expectError(CustomerAlreadyInactiveException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).saveOrUpdateCustomer(any());
    }

    @Test
    @DisplayName("Debería lanzar EmptyCustomerIdException cuando ID es vacío en eliminación")
    void deleteByIdCustomer_WithEmptyId_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.deleteByIdCustomer(""))
                .expectError(EmptyCustomerIdException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
    }

    @Test
    @DisplayName("Debería lanzar CustomerNotFoundException cuando cliente no existe para eliminar")
    void deleteByIdCustomer_WithNonExistentId_ShouldThrowException() {
        // Arrange
        String nonExistentId = "non-existent-id";
        when(customerRepositoryOutputPort.findByIdCustomer(nonExistentId))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(customerService.deleteByIdCustomer(nonExistentId))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA BUSCAR POR TIPO Y NÚMERO DE DOCUMENTO
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería encontrar cliente por tipo y número de documento exitosamente")
    void findByDocumentTypeAndDocumentNumber_ShouldReturnCustomer() {
        // Arrange
        DocumentType documentType = DocumentType.DNI;
        String documentNumber = "12345678";

        when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber))
                .thenReturn(Mono.just(activeCustomer));
        when(customerResponseMapper.toSingletonResponse(activeCustomer)).thenReturn(customerListResponse);

        // Act & Assert
        StepVerifier.create(customerService.findByDocumentTypeAndDocumentNumber(documentType, documentNumber))
                .expectNext(customerListResponse)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar InvalidDocumentException cuando tipo de documento es null")
    void findByDocumentTypeAndDocumentNumber_WithNullDocumentType_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByDocumentTypeAndDocumentNumber(null, "12345678"))
                .expectError(InvalidDocumentException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByDocumentTypeAndDocumentNumber(any(), anyString());
    }

    @Test
    @DisplayName("Debería lanzar InvalidDocumentException cuando número de documento es vacío")
    void findByDocumentTypeAndDocumentNumber_WithEmptyDocumentNumber_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, ""))
                .expectError(InvalidDocumentException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByDocumentTypeAndDocumentNumber(any(), anyString());
    }

    @Test
    @DisplayName("Debería lanzar CustomerNotFoundException cuando cliente no existe por documento")
    void findByDocumentTypeAndDocumentNumber_WithNonExistentDocument_ShouldThrowException() {
        // Arrange
        DocumentType documentType = DocumentType.DNI;
        String documentNumber = "99999999";

        when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(documentType, documentNumber))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(customerService.findByDocumentTypeAndDocumentNumber(documentType, documentNumber))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    // ------------------------------------------------------------
    // PRUEBAS PARA BUSCAR POR NÚMERO DE DOCUMENTO
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería encontrar cliente por número de documento exitosamente")
    void findByDocumentNumber_ShouldReturnCustomer() {
        // Arrange
        String documentNumber = "12345678";

        when(customerRepositoryOutputPort.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.just(activeCustomer));
        when(customerResponseMapper.toSingletonResponse(activeCustomer)).thenReturn(customerListResponse);

        // Act & Assert
        StepVerifier.create(customerService.findByDocumentNumber(documentNumber))
                .expectNext(customerListResponse)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar InvalidDocumentException cuando número de documento es null")
    void findByDocumentNumber_WithNullDocumentNumber_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByDocumentNumber(null))
                .expectError(InvalidDocumentException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByDocumentNumber(anyString());
    }

    @Test
    @DisplayName("Debería lanzar InvalidDocumentException cuando número de documento es vacío")
    void findByDocumentNumber_WithEmptyDocumentNumber_ShouldThrowException() {
        // Act & Assert
        StepVerifier.create(customerService.findByDocumentNumber(""))
                .expectError(InvalidDocumentException.class)
                .verify();

        verify(customerRepositoryOutputPort, never()).findByDocumentNumber(anyString());
    }

    @Test
    @DisplayName("Debería lanzar CustomerNotFoundException cuando cliente no existe por número de documento")
    void findByDocumentNumber_WithNonExistentDocument_ShouldThrowException() {
        // Arrange
        String documentNumber = "99999999";

        when(customerRepositoryOutputPort.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(customerService.findByDocumentNumber(documentNumber))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    // ------------------------------------------------------------
    // PRUEBAS DE ERRORES INESPERADOS
    // ------------------------------------------------------------

    @Test
    @DisplayName("Debería manejar errores inesperados en saveCustomer")
    void saveCustomer_WithUnexpectedError_ShouldThrowCustomerServiceException() {
        // Arrange
        when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
                customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act & Assert
        StepVerifier.create(customerService.saveCustomer(customerRequest))
                .expectError(CustomerServiceException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería manejar errores inesperados en updateCustomer")
    void updateCustomer_WithUnexpectedError_ShouldThrowCustomerServiceException() {
        // Arrange
        when(customerRepositoryOutputPort.findByIdCustomer(customerRequest.getId()))
                .thenReturn(Mono.just(activeCustomer));
        when(customerRepositoryOutputPort.saveOrUpdateCustomer(any()))
                .thenReturn(Mono.error(new RuntimeException("Error de base de datos")));

        // Act & Assert
        StepVerifier.create(customerService.updateCustomer(customerRequest))
                .expectError(CustomerServiceException.class)
                .verify();
    }
}