package com.nttdata.customer_service.infrastructure.entity;

import com.nttdata.customer_service.domain.model.CustomerType;
import com.nttdata.customer_service.domain.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity {

    @Id
    private String id;
    private DocumentType documentType;
    private String documentNumber;
    private String fullName;
    @Field("businessName")
    private String businessName;
    private String email;
    private String phoneNumber;
    private String address;
    private CustomerType customerType;
    private String createdAt;
    private String updatedAt;

}
