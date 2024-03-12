package br.com.customeraccountintegration.camel.adapter.repository;

import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<CustomerModel, String> {
  List<CustomerModel> findByCustomerStatus(CustomerStatusEnum customerStatus);
  long countByCustomerStatus(CustomerStatusEnum customerStatus);
}
