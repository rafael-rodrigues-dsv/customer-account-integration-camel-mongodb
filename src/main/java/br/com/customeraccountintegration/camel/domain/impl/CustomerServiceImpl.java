package br.com.customeraccountintegration.camel.domain.impl;

import br.com.customeraccountintegration.camel.adapter.repository.CustomerRepository;
import br.com.customeraccountintegration.camel.domain.CustomerService;
import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;

  @Override
  public List<CustomerModel> findByStatus(CustomerStatusEnum customerStatus) {
    return customerRepository.findByCustomerStatus(customerStatus);
  }

  @Override
  public CustomerModel findById(String id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new EmptyResultDataAccessException("Customer not found with id " + id, 1));
  }

  @Override
  public long countByStatus(CustomerStatusEnum customerStatus) {
    return customerRepository.countByCustomerStatus(customerStatus);
  }

  @Override
  public CustomerModel updateStatus(String id, CustomerStatusEnum customerStatus) {
    return customerRepository.findById(id).map(existingCustomer -> {
      existingCustomer.setCustomerStatus(customerStatus);
      existingCustomer.setUpdateDate(LocalDateTime.now());
      return customerRepository.save(existingCustomer);
    }).orElseThrow(() -> new EmptyResultDataAccessException("Customer not found with id " + id, 1));
  }

  @Override
  public void addBatch(List<CustomerModel> customers) {
    customers.forEach(customer -> {
      customer.setCreationDate(LocalDateTime.now());
    });
    customerRepository.saveAll(customers);
  }
}
