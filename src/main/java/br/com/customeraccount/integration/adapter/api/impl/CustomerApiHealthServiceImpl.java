package br.com.customeraccount.integration.adapter.api.impl;

import br.com.customeraccount.integration.adapter.api.CustomerApiHealthService;
import br.com.customeraccount.integration.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccount.integration.domain.model.CustomerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerApiHealthServiceImpl implements CustomerApiHealthService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerApiHealthServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.health.url}")
    private String apiUrl;

    @Override
    public CustomerModel syncCustomer(CustomerModel customerModel) {
        try {
            ResponseEntity<Void> responseEntity = restTemplate.getForEntity(apiUrl, Void.class);
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                customerModel.setCustomerStatus(CustomerStatusEnum.SUCCESSFUL_PROCESSING);
            } else {
                customerModel.setCustomerStatus(CustomerStatusEnum.PROCESSING_FAILURE);
                customerModel.setErrorMessage(statusCode.value() + " - " + responseEntity.getBody());
            }
        } catch (HttpStatusCodeException e) {
            customerModel.setCustomerStatus(CustomerStatusEnum.PROCESSING_FAILURE);
            customerModel.setErrorMessage(e.getStatusCode().value() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Erro ao enviar a requisição para a API: {}", e.getMessage(), e);
            customerModel.setCustomerStatus(CustomerStatusEnum.PROCESSING_FAILURE);
            customerModel.setErrorMessage("Erro ao processar a requisição: " + e.getMessage());
        }

        return customerModel;
    }
}
