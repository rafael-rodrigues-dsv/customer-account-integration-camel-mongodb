package br.com.customeraccountintegration.camel.adapter.api;

import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import org.springframework.http.HttpStatus;

public interface CustomerApiHealthService {

  HttpStatus checkApiHealth(CustomerModel customerModel);
}
