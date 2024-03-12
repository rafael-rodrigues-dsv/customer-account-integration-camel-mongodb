package br.com.customeraccountintegration.camel.domain.model;

import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
  private String id = UUID.randomUUID().toString();  // Gerando automaticamente um GUID

  private String documentNumber;
  private CustomerStatusEnum customerStatus;
  private String email;
  private String contract;

  @Transient
  private boolean isNew = true;

  @Transient
  private LocalDateTime creationDate;

  @Transient
  private LocalDateTime updateDate;

  public void configureBeforeSave() {
    this.creationDate = LocalDateTime.now();
    this.updateDate = this.creationDate;
  }

  public void configureBeforeUpdate() {
    this.updateDate = LocalDateTime.now();
  }
}
