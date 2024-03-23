package br.com.customeraccount.integration.adapter.api;

import br.com.customeraccount.integration.domain.model.CustomerModel;

public interface CustomerApiHealthService {

    CustomerModel syncCustomer(CustomerModel customerModel);
}
