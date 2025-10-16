package com.nttdata.customer_service.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    private String id;

    @NotBlank(message = "El tipo de documento no puede estar vacío")
    private String documentType;

    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentNumber;

    @NotBlank(message = "El nombre completo no puede estar vacío")
    private String fullName;

    private String businessName;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    private String email;

    private String phoneNumber;

    private String address;

    @NotBlank(message = "El tipo de cliente no puede estar vacío")
    private String customerType;
}
