package com.nttdata.customer_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    private  String id;
    private  DocumentType documentType;
    private  String documentNumber;
    private  String fullName;
    private  String businessName;
    private  String email;
    private  String phoneNumber;
    private String address;
    private CustomerType customerType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StatusType status;
}

