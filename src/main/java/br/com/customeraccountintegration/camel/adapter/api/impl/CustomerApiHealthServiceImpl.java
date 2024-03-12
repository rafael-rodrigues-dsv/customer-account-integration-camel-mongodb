package br.com.customeraccountintegration.camel.adapter.api.impl;

import br.com.customeraccountintegration.camel.adapter.api.CustomerApiHealthService;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerApiHealthServiceImpl implements CustomerApiHealthService {

  private static final Logger logger = LoggerFactory.getLogger(CustomerApiHealthServiceImpl.class);

  @Autowired
  private RestTemplate restTemplate;

  @Value("${api.health.url}")
  private String apiUrl;

  @Override
  public HttpStatus checkApiHealth(CustomerModel customerModel) {
    try {
      ResponseEntity<Void> responseEntity = restTemplate.getForEntity(apiUrl, Void.class);
      return (HttpStatus) responseEntity.getStatusCode();
    } catch (Exception e) {
      logger.error("Erro ao verificar a saúde da API: {}", e.getMessage(), e);
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}
