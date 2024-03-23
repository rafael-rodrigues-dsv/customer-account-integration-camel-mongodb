package br.com.customeraccount.integration.domain.model;

import br.com.customeraccount.integration.domain.enumeration.CustomerStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer")
public class CustomerModel {

    @Id
    private String id = UUID.randomUUID().toString();
    private String documentNumber;
    private CustomerStatusEnum customerStatus;
    private String email;
    private String contract;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime updateDate;
    private String errorMessage;
}
