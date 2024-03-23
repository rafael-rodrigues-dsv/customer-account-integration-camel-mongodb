package br.com.customeraccount.integration.domain;

import br.com.customeraccount.integration.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccount.integration.domain.model.CustomerModel;

import java.util.List;

public interface CustomerService {

    List<CustomerModel> findByStatus(CustomerStatusEnum customerStatus);
    CustomerModel findById(String id);
    long countByStatus(CustomerStatusEnum customerStatus);
    CustomerModel updateCustomer(String id, CustomerModel customerModel);
    void addBatch(List<CustomerModel> customers);
}

