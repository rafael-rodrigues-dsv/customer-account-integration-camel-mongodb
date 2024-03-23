package br.com.customeraccount.integration.adapter.repository;

import br.com.customeraccount.integration.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccount.integration.domain.model.CustomerModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<CustomerModel, String> {
    List<CustomerModel> findByCustomerStatus(CustomerStatusEnum customerStatus);
    long countByCustomerStatus(CustomerStatusEnum customerStatus);
}
