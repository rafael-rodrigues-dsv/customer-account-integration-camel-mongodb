package br.com.customeraccountintegration.camel.domain;

import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;

import java.util.List;

public interface CustomerService {

  List<CustomerModel> findByStatus(CustomerStatusEnum customerStatus);

  CustomerModel findById(String id);

  long countByStatus(CustomerStatusEnum customerStatus);

  CustomerModel updateStatus(String id, CustomerStatusEnum customerStatus);

  void addBatch(List<CustomerModel> customers);
}

