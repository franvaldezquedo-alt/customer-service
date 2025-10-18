package com.nttdata.customer_service.infrastructure.utils;

import com.nttdata.customer_service.domain.error.CustomerErrorFactory;
import com.nttdata.customer_service.domain.error.CustomerNotFoundExeptions;
import com.nttdata.customer_service.domain.model.*;
import com.nttdata.customer_service.infrastructure.entity.CustomerEntity;
import com.nttdata.customer_service.infrastructure.model.CustomerRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomerUtils {


    /**
     * Convierte una lista de entidades de cliente a respuesta con lista
     */
    public static CustomerListResponse convertCustomerListResponse(List<CustomerEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return CustomerListResponse.builder()
                    .data(Collections.emptyList())
                    .Error(null)
                    .build();
        }

        return CustomerListResponse.builder()
                .data(entities.stream()
                        .map(CustomerUtils::convertToCustomerResponse)
                        .collect(Collectors.toList()))
                .Error(null)
                .build();
    }

    /**
     * Convierte una entidad de cliente a modelo de dominio
     */
    public static Customer convertToCustomerResponse(CustomerEntity entity) {
        return Customer.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .businessName(entity.getBusinessName())
                .documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber())
                .customerType(entity.getCustomerType())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convierte una única entidad a respuesta con lista singleton
     */
    public static CustomerListResponse convertCustomerSingletonResponse(CustomerEntity entity) {
        return CustomerListResponse.builder()
                .data(Collections.singletonList(convertToCustomerResponse(entity)))
                .Error(null)
                .build();
    }

    /**
     * Convierte un request a entidad para creación (sin ID)
     */
    public static CustomerEntity convertRequestToEntity(CustomerRequest request) {
        return CustomerEntity.builder()
                .documentType(parseDocumentType(request.getDocumentType()))
                .documentNumber(request.getDocumentNumber())
                .fullName(request.getFullName())
                .businessName(request.getBusinessName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .customerType(parseCustomerType(request.getCustomerType()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte un request a entidad para actualización (con ID y preservando createdAt)
     */
    public static CustomerEntity convertCustomerUpdateEntity(CustomerRequest request) {
        String currentTimestamp = getCurrentTimestamp();
        return CustomerEntity.builder()
                .id(request.getId())
                .documentType(parseDocumentType(request.getDocumentType()))
                .documentNumber(request.getDocumentNumber())
                .fullName(request.getFullName())
                .businessName(request.getBusinessName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .customerType(parseCustomerType(request.getCustomerType()))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte entidad guardada a respuesta de operación exitosa
     */
    public static CustomerResponse convertEntityToResponse(CustomerEntity entity) {
        return createSuccessResponse(Constants.CUSTOMER_CREATED, entity.getId());
    }

    /**
     * Crea respuesta para operación de eliminación
     */
    public static CustomerResponse convertCustomerResponseDelete(String customerId) {
        return createSuccessResponse(Constants.CUSTOMER_DELETED, customerId);
    }

    /**
     * Crea respuesta para operación de actualización
     */
    public static CustomerResponse convertCustomerResponseUpdate(CustomerEntity entity) {
        return createSuccessResponse(Constants.CUSTOMER_UPDATED, entity.getId());
    }

    /**
     * Maneja errores devolviendo Mono con excepción apropiada
     */
    public static Mono<CustomerListResponse> handleErrorCustomerMono(Throwable error) {
        log.error("Error en operación de cliente: {}", error.getMessage());

        // Si es CustomerNotFoundExeptions, propagar con respuesta estructurada
        if (error instanceof CustomerNotFoundExeptions) {
            return Mono.just(CustomerListResponse.builder()
                    .data(Collections.emptyList())
                    .Error(error.getMessage())
                    .build());
        }

        // Para otros errores, propagar la excepción
        return Mono.error(CustomerErrorFactory.createException(error));
    }

    /**
     * Maneja errores devolviendo CustomerResponse con información del error
     * NOTA: Solo usar para casos donde NO queremos propagar la excepción
     */
    public static Mono<CustomerResponse> handleErrorCustomerResponse(Throwable error) {
        log.error("Error manejado en respuesta: {}", error.getMessage());

        // Si es CustomerNotFoundExeptions, devolver 404
        if (error instanceof CustomerNotFoundExeptions) {
            return Mono.just(createErrorResponse(404, error.getMessage()));
        }

        // Para otros errores, devolver 500
        return Mono.just(createErrorResponse(Constants.HTTP_INTERNAL_ERROR,
                Constants.ERROR_PROCESSING + error.getMessage()));
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private static CustomerResponse createSuccessResponse(String message, String entityId) {
        return CustomerResponse.builder()
                .codResponse(Constants.HTTP_OK)
                .messageResponse(message)
                .codEntity(entityId)
                .build();
    }

    private static CustomerResponse createErrorResponse(int code, String message) {
        return CustomerResponse.builder()
                .codResponse(code)
                .messageResponse(message)
                .codEntity(null)
                .build();
    }

    /**
     * Crea una respuesta de lista de clientes con error
     */
    public static CustomerListResponse createErrorListResponse(String errorMessage) {
        return CustomerListResponse.builder()
                .data(Collections.emptyList())
                .Error(errorMessage)
                .build();
    }

    private static String getCurrentTimestamp() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    private static DocumentType parseDocumentType(String documentType) {
        return Optional.ofNullable(documentType)
                .map(String::toUpperCase)
                .map(String::trim)
                .map(DocumentType::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento inválido"));
    }

    private static CustomerType parseCustomerType(String customerType) {
        return Optional.ofNullable(customerType)
                .map(String::toUpperCase)
                .map(String::trim)
                .map(CustomerType::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cliente inválido"));
    }
}