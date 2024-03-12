package br.com.customeraccountintegration.camel.adapter.api;

import br.com.customeraccountintegration.camel.domain.model.CustomerModel;

public interface CustomerApiHealthService {

  CustomerModel syncCustomer(CustomerModel customerModel);
}
