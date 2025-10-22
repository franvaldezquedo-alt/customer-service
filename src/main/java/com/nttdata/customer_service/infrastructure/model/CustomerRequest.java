package com.nttdata.customer_service.infrastructure.model;

import com.nttdata.customer_service.domain.model.CustomerType;
import com.nttdata.customer_service.domain.model.DocumentType;
import com.nttdata.customer_service.domain.model.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    private String id;

    @NotNull(message = "El tipo de documento no puede estar vacío")
    private DocumentType documentType;

    @NotBlank(message = "El número de documento no puede estar vacío")
    @Size(max = 20, message = "El número de documento no puede exceder los 20 caracteres")
    private String documentNumber;

    @NotBlank(message = "El nombre completo no puede estar vacío")
    @Size(max = 100)
    private String fullName;

    private String businessName;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;

    private String phoneNumber;

    private String address;

    @NotNull(message = "El tipo de cliente no puede estar vacío")
    private CustomerType customerType;
}
