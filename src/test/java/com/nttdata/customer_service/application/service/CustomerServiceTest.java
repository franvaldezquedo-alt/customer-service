package com.nttdata.customer_service.application.service;

import com.nttdata.customer_service.application.port.out.CustomerRepositoryOutputPort;
import com.nttdata.customer_service.domain.error.*;
import com.nttdata.customer_service.domain.model.*;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
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
  private CustomerListResponse customerListResponse;
  private CustomerResponse customerResponse;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    activeCustomer = Customer.builder()
          .id("123")
          .documentType(DocumentType.DNI)
          .documentNumber("12345678")
          .fullName("John Doe")
          .businessName(null)
          .email("john@example.com")
          .phoneNumber("987654321")
          .address("123 Main St")
          .customerType(CustomerType.PERSONAL)
          .createdAt(now)
          .updatedAt(now)
          .status(StatusType.ACTIVE)
          .build();

    inactiveCustomer = Customer.builder()
          .id("456")
          .documentType(DocumentType.RUC)
          .documentNumber("20123456789")
          .fullName(null)
          .businessName("ACME Corp")
          .email("acme@example.com")
          .phoneNumber("987654322")
          .address("456 Corp St")
          .customerType(CustomerType.BUSINESS)
          .createdAt(now)
          .updatedAt(now)
          .status(StatusType.INACTIVE)
          .build();

    customerRequest = CustomerRequest.builder()
          .documentType(DocumentType.DNI)
          .documentNumber("12345678")
          .fullName("John Doe")
          .email("john@example.com")
          .phoneNumber("987654321")
          .address("123 Main St")
          .customerType(CustomerType.PERSONAL)
          .build();

    customerListResponse = CustomerListResponse.builder()
          .data(Arrays.asList(activeCustomer))
          .error(null)
          .build();

    customerResponse = CustomerResponse.builder()
          .codResponse(200)
          .messageResponse("Operación exitosa")
          .codEntity("123")
          .build();
  }

  // ============================================================
  // TESTS PARA findAllCustomer
  // ============================================================

  @Test
  @DisplayName("findAllCustomer - Debe retornar solo clientes activos")
  void findAllCustomer_ShouldReturnOnlyActiveCustomers() {
    // Arrange
    when(customerRepositoryOutputPort.findAllCustomer())
          .thenReturn(Flux.just(activeCustomer, inactiveCustomer));
    when(customerResponseMapper.toCustomerListResponse(anyList()))
          .thenReturn(customerListResponse);

    // Act
    Mono<CustomerListResponse> result = customerService.findAllCustomer();

    // Assert
    StepVerifier.create(result)
          .expectNextMatches(response ->
                response.getData().size() == 1 &&
                      response.getData().get(0).getStatus() == StatusType.ACTIVE
          )
          .verifyComplete();

    verify(customerRepositoryOutputPort, times(1)).findAllCustomer();
    verify(customerResponseMapper, times(1)).toCustomerListResponse(anyList());
  }

  @Test
  @DisplayName("findAllCustomer - Debe retornar lista vacía cuando no hay clientes activos")
  void findAllCustomer_ShouldReturnEmptyListWhenNoActiveCustomers() {
    // Arrange
    when(customerRepositoryOutputPort.findAllCustomer())
          .thenReturn(Flux.just(inactiveCustomer));
    when(customerResponseMapper.toCustomerListResponse(anyList()))
          .thenReturn(CustomerListResponse.builder().data(List.of()).build());

    // Act
    Mono<CustomerListResponse> result = customerService.findAllCustomer();

    // Assert
    StepVerifier.create(result)
          .expectNextMatches(response -> response.getData().isEmpty())
          .verifyComplete();
  }

  @Test
  @DisplayName("findAllCustomer - Debe propagar error cuando falla la consulta")
  void findAllCustomer_ShouldPropagateErrorWhenQueryFails() {
    // Arrange
    when(customerRepositoryOutputPort.findAllCustomer())
          .thenReturn(Flux.error(new RuntimeException("Database error")));

    // Act
    Mono<CustomerListResponse> result = customerService.findAllCustomer();

    // Assert
    StepVerifier.create(result)
          .expectError(RuntimeException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA findByIdCustomer
  // ============================================================

  @Test
  @DisplayName("findByIdCustomer - Debe retornar cliente cuando existe")
  void findByIdCustomer_ShouldReturnCustomerWhenExists() {
    // Arrange
    String customerId = "123";
    when(customerRepositoryOutputPort.findByIdCustomer(customerId))
          .thenReturn(Mono.just(activeCustomer));
    when(customerResponseMapper.toSingletonResponse(activeCustomer))
          .thenReturn(customerListResponse);

    // Act
    Mono<CustomerListResponse> result = customerService.findByIdCustomer(customerId);

    // Assert
    StepVerifier.create(result)
          .expectNext(customerListResponse)
          .verifyComplete();

    verify(customerRepositoryOutputPort, times(1)).findByIdCustomer(customerId);
  }

  @Test
  @DisplayName("findByIdCustomer - Debe lanzar EmptyCustomerIdException cuando ID es vacío")
  void findByIdCustomer_ShouldThrowEmptyCustomerIdExceptionWhenIdIsEmpty() {
    // Arrange
    String emptyId = "   ";

    // Act
    Mono<CustomerListResponse> result = customerService.findByIdCustomer(emptyId);

    // Assert
    StepVerifier.create(result)
          .expectError(EmptyCustomerIdException.class)
          .verify();

    verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
  }

  @Test
  @DisplayName("findByIdCustomer - Debe lanzar CustomerNotFoundException cuando no existe")
  void findByIdCustomer_ShouldThrowCustomerNotFoundExceptionWhenNotExists() {
    // Arrange
    String customerId = "999";
    when(customerRepositoryOutputPort.findByIdCustomer(customerId))
          .thenReturn(Mono.empty());

    // Act
    Mono<CustomerListResponse> result = customerService.findByIdCustomer(customerId);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerNotFoundException.class)
          .verify();
  }

  @Test
  @DisplayName("findByIdCustomer - Debe lanzar EmptyCustomerIdException cuando ID es null")
  void findByIdCustomer_ShouldThrowEmptyCustomerIdExceptionWhenIdIsNull() {
    // Arrange
    String nullId = null;

    // Act
    Mono<CustomerListResponse> result = customerService.findByIdCustomer(nullId);

    // Assert
    StepVerifier.create(result)
          .expectError(EmptyCustomerIdException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA saveCustomer
  // ============================================================

  @Test
  @DisplayName("saveCustomer - Debe crear cliente exitosamente cuando no existe")
  void saveCustomer_ShouldCreateCustomerSuccessfullyWhenNotExists() {
    // Arrange
    when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
          customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
          .thenReturn(Mono.empty());
    when(customerMapper.fromRequest(customerRequest)).thenReturn(activeCustomer);
    when(customerRepositoryOutputPort.saveOrUpdateCustomer(activeCustomer))
          .thenReturn(Mono.just(activeCustomer));
    when(customerResponseMapper.toSuccessResponse(anyString(), anyString()))
          .thenReturn(customerResponse);

    // Act
    Mono<CustomerResponse> result = customerService.saveCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectNextMatches(response ->
                response.getCodEntity().equals("123") &&
                      response.getMessageResponse().equals("Operación exitosa") &&
                      response.getCodResponse() == 200
          )
          .verifyComplete();

    verify(customerMapper, times(1)).fromRequest(customerRequest);
    verify(customerRepositoryOutputPort, times(1)).saveOrUpdateCustomer(activeCustomer);
  }

  @Test
  @DisplayName("saveCustomer - Debe lanzar CustomerAlreadyExistsException cuando cliente ya existe")
  void saveCustomer_ShouldThrowCustomerAlreadyExistsExceptionWhenExists() {
    // Arrange
    when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
          customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
          .thenReturn(Mono.just(activeCustomer));

    // Act
    Mono<CustomerResponse> result = customerService.saveCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerAlreadyExistsException.class)
          .verify();

    verify(customerRepositoryOutputPort, never()).saveOrUpdateCustomer(any());
  }

  @Test
  @DisplayName("saveCustomer - Debe lanzar CustomerServiceException cuando ocurre error inesperado")
  void saveCustomer_ShouldThrowCustomerServiceExceptionOnUnexpectedError() {
    // Arrange
    when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(
          customerRequest.getDocumentType(), customerRequest.getDocumentNumber()))
          .thenReturn(Mono.empty());
    when(customerMapper.fromRequest(customerRequest)).thenReturn(activeCustomer);
    when(customerRepositoryOutputPort.saveOrUpdateCustomer(activeCustomer))
          .thenReturn(Mono.error(new RuntimeException("Database error")));

    // Act
    Mono<CustomerResponse> result = customerService.saveCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerServiceException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA updateCustomer
  // ============================================================

  @Test
  @DisplayName("updateCustomer - Debe actualizar cliente exitosamente")
  void updateCustomer_ShouldUpdateCustomerSuccessfully() {
    // Arrange
    customerRequest.setId("123");
    Customer updatedCustomer = Customer.builder()
          .id("123")
          .documentType(customerRequest.getDocumentType())
          .documentNumber(customerRequest.getDocumentNumber())
          .fullName("John Updated")
          .status(StatusType.ACTIVE)
          .build();

    when(customerRepositoryOutputPort.findByIdCustomer("123"))
          .thenReturn(Mono.just(activeCustomer));
    when(customerMapper.updateFromRequest(activeCustomer, customerRequest))
          .thenReturn(updatedCustomer);
    when(customerRepositoryOutputPort.saveOrUpdateCustomer(updatedCustomer))
          .thenReturn(Mono.just(updatedCustomer));
    when(customerResponseMapper.toSuccessResponse(anyString(), anyString()))
          .thenReturn(customerResponse);

    // Act
    Mono<CustomerResponse> result = customerService.updateCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectNextMatches(response ->
                response.getCodEntity().equals("123") &&
                      response.getCodResponse() == 200
          )
          .verifyComplete();

    verify(customerRepositoryOutputPort, times(1)).findByIdCustomer("123");
    verify(customerRepositoryOutputPort, times(1)).saveOrUpdateCustomer(updatedCustomer);
  }

  @Test
  @DisplayName("updateCustomer - Debe lanzar EmptyCustomerIdException cuando ID es vacío")
  void updateCustomer_ShouldThrowEmptyCustomerIdExceptionWhenIdIsEmpty() {
    // Arrange
    customerRequest.setId("  ");

    // Act
    Mono<CustomerResponse> result = customerService.updateCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectError(EmptyCustomerIdException.class)
          .verify();

    verify(customerRepositoryOutputPort, never()).findByIdCustomer(anyString());
  }

  @Test
  @DisplayName("updateCustomer - Debe lanzar CustomerNotFoundException cuando cliente no existe")
  void updateCustomer_ShouldThrowCustomerNotFoundExceptionWhenNotExists() {
    // Arrange
    customerRequest.setId("999");
    when(customerRepositoryOutputPort.findByIdCustomer("999"))
          .thenReturn(Mono.empty());

    // Act
    Mono<CustomerResponse> result = customerService.updateCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerNotFoundException.class)
          .verify();
  }

  @Test
  @DisplayName("updateCustomer - Debe lanzar CustomerServiceException en error inesperado")
  void updateCustomer_ShouldThrowCustomerServiceExceptionOnUnexpectedError() {
    // Arrange
    customerRequest.setId("123");
    when(customerRepositoryOutputPort.findByIdCustomer("123"))
          .thenReturn(Mono.just(activeCustomer));
    when(customerMapper.updateFromRequest(any(), any()))
          .thenReturn(activeCustomer);
    when(customerRepositoryOutputPort.saveOrUpdateCustomer(any()))
          .thenReturn(Mono.error(new RuntimeException("Database error")));

    // Act
    Mono<CustomerResponse> result = customerService.updateCustomer(customerRequest);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerServiceException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA deleteByIdCustomer
  // ============================================================

  @Test
  @DisplayName("deleteByIdCustomer - Debe desactivar cliente exitosamente")
  void deleteByIdCustomer_ShouldDeactivateCustomerSuccessfully() {
    // Arrange
    String customerId = "123";
    when(customerRepositoryOutputPort.findByIdCustomer(customerId))
          .thenReturn(Mono.just(activeCustomer));
    when(customerRepositoryOutputPort.saveOrUpdateCustomer(any(Customer.class)))
          .thenReturn(Mono.just(activeCustomer));
    when(customerResponseMapper.toSuccessResponse(anyString(), anyString()))
          .thenReturn(customerResponse);

    // Act
    Mono<CustomerResponse> result = customerService.deleteByIdCustomer(customerId);

    // Assert
    StepVerifier.create(result)
          .expectNextMatches(response ->
                response.getCodEntity().equals("123") &&
                      response.getCodResponse() == 200
          )
          .verifyComplete();

    verify(customerRepositoryOutputPort, times(1)).findByIdCustomer(customerId);
    verify(customerRepositoryOutputPort, times(1)).saveOrUpdateCustomer(any(Customer.class));
  }

  @Test
  @DisplayName("deleteByIdCustomer - Debe lanzar CustomerAlreadyInactiveException cuando ya está inactivo")
  void deleteByIdCustomer_ShouldThrowCustomerAlreadyInactiveExceptionWhenAlreadyInactive() {
    // Arrange
    String customerId = "456";
    when(customerRepositoryOutputPort.findByIdCustomer(customerId))
          .thenReturn(Mono.just(inactiveCustomer));

    // Act
    Mono<CustomerResponse> result = customerService.deleteByIdCustomer(customerId);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerAlreadyInactiveException.class)
          .verify();

    verify(customerRepositoryOutputPort, never()).saveOrUpdateCustomer(any());
  }

  @Test
  @DisplayName("deleteByIdCustomer - Debe lanzar EmptyCustomerIdException cuando ID es vacío")
  void deleteByIdCustomer_ShouldThrowEmptyCustomerIdExceptionWhenIdIsEmpty() {
    // Arrange
    String emptyId = "";

    // Act
    Mono<CustomerResponse> result = customerService.deleteByIdCustomer(emptyId);

    // Assert
    StepVerifier.create(result)
          .expectError(EmptyCustomerIdException.class)
          .verify();
  }

  @Test
  @DisplayName("deleteByIdCustomer - Debe lanzar CustomerNotFoundException cuando no existe")
  void deleteByIdCustomer_ShouldThrowCustomerNotFoundExceptionWhenNotExists() {
    // Arrange
    String customerId = "999";
    when(customerRepositoryOutputPort.findByIdCustomer(customerId))
          .thenReturn(Mono.empty());

    // Act
    Mono<CustomerResponse> result = customerService.deleteByIdCustomer(customerId);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerNotFoundException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA findByDocumentTypeAndDocumentNumber
  // ============================================================

  @Test
  @DisplayName("findByDocumentTypeAndDocumentNumber - Debe retornar cliente cuando existe")
  void findByDocumentTypeAndDocumentNumber_ShouldReturnCustomerWhenExists() {
    // Arrange
    DocumentType docType = DocumentType.DNI;
    String docNumber = "12345678";
    when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(docType, docNumber))
          .thenReturn(Mono.just(activeCustomer));
    when(customerResponseMapper.toSingletonResponse(activeCustomer))
          .thenReturn(customerListResponse);

    // Act
    Mono<CustomerListResponse> result = customerService
          .findByDocumentTypeAndDocumentNumber(docType, docNumber);

    // Assert
    StepVerifier.create(result)
          .expectNext(customerListResponse)
          .verifyComplete();
  }

  @Test
  @DisplayName("findByDocumentTypeAndDocumentNumber - Debe lanzar InvalidDocumentException con tipo null")
  void findByDocumentTypeAndDocumentNumber_ShouldThrowInvalidDocumentExceptionWhenTypeIsNull() {
    // Arrange
    DocumentType docType = null;
    String docNumber = "12345678";

    // Act
    Mono<CustomerListResponse> result = customerService
          .findByDocumentTypeAndDocumentNumber(docType, docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(InvalidDocumentException.class)
          .verify();
  }

  @Test
  @DisplayName("findByDocumentTypeAndDocumentNumber - Debe lanzar InvalidDocumentException con número vacío")
  void findByDocumentTypeAndDocumentNumber_ShouldThrowInvalidDocumentExceptionWhenNumberIsBlank() {
    // Arrange
    DocumentType docType = DocumentType.DNI;
    String docNumber = "   ";

    // Act
    Mono<CustomerListResponse> result = customerService
          .findByDocumentTypeAndDocumentNumber(docType, docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(InvalidDocumentException.class)
          .verify();
  }

  @Test
  @DisplayName("findByDocumentTypeAndDocumentNumber - Debe lanzar CustomerNotFoundException cuando no existe")
  void findByDocumentTypeAndDocumentNumber_ShouldThrowCustomerNotFoundExceptionWhenNotExists() {
    // Arrange
    DocumentType docType = DocumentType.DNI;
    String docNumber = "99999999";
    when(customerRepositoryOutputPort.findByDocumentTypeAndDocumentNumber(docType, docNumber))
          .thenReturn(Mono.empty());

    // Act
    Mono<CustomerListResponse> result = customerService
          .findByDocumentTypeAndDocumentNumber(docType, docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerNotFoundException.class)
          .verify();
  }

  // ============================================================
  // TESTS PARA findByDocumentNumber
  // ============================================================

  @Test
  @DisplayName("findByDocumentNumber - Debe retornar cliente cuando existe")
  void findByDocumentNumber_ShouldReturnCustomerWhenExists() {
    // Arrange
    String docNumber = "12345678";
    when(customerRepositoryOutputPort.findByDocumentNumber(docNumber))
          .thenReturn(Mono.just(activeCustomer));
    when(customerResponseMapper.toSingletonResponse(activeCustomer))
          .thenReturn(customerListResponse);

    // Act
    Mono<CustomerListResponse> result = customerService.findByDocumentNumber(docNumber);

    // Assert
    StepVerifier.create(result)
          .expectNext(customerListResponse)
          .verifyComplete();
  }

  @Test
  @DisplayName("findByDocumentNumber - Debe lanzar InvalidDocumentException cuando número es null")
  void findByDocumentNumber_ShouldThrowInvalidDocumentExceptionWhenNumberIsNull() {
    // Arrange
    String docNumber = null;

    // Act
    Mono<CustomerListResponse> result = customerService.findByDocumentNumber(docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(InvalidDocumentException.class)
          .verify();
  }

  @Test
  @DisplayName("findByDocumentNumber - Debe lanzar InvalidDocumentException cuando número es vacío")
  void findByDocumentNumber_ShouldThrowInvalidDocumentExceptionWhenNumberIsBlank() {
    // Arrange
    String docNumber = "  ";

    // Act
    Mono<CustomerListResponse> result = customerService.findByDocumentNumber(docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(InvalidDocumentException.class)
          .verify();
  }

  @Test
  @DisplayName("findByDocumentNumber - Debe lanzar CustomerNotFoundException cuando no existe")
  void findByDocumentNumber_ShouldThrowCustomerNotFoundExceptionWhenNotExists() {
    // Arrange
    String docNumber = "99999999";
    when(customerRepositoryOutputPort.findByDocumentNumber(docNumber))
          .thenReturn(Mono.empty());

    // Act
    Mono<CustomerListResponse> result = customerService.findByDocumentNumber(docNumber);

    // Assert
    StepVerifier.create(result)
          .expectError(CustomerNotFoundException.class)
          .verify();
  }
}