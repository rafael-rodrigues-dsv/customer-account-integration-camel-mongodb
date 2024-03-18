package br.com.customeraccountintegration.camel.adapter.integration.processor.impl;

import br.com.customeraccountintegration.camel.adapter.integration.processor.CustomerAccountSaveBatchProcessor;
import br.com.customeraccountintegration.camel.domain.CustomerService;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountSaveBatchProcessorImpl implements CustomerAccountSaveBatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CustomerAccountSaveBatchProcessorImpl.class);
    private final CustomerService customerService;

    @Override
    public void process(Exchange exchange) throws Exception {
        long batchStartTime = System.currentTimeMillis(); // Marca o início do processamento em lote
        List<CustomerModel> customerList = exchange.getProperty("customerList", List.class);

        if (customerList != null && !customerList.isEmpty()) {
            logger.info("Salvando em lote - Total de registros: {}", customerList.size());
            customerService.addBatch(customerList);
        }

        long batchEndTime = System.currentTimeMillis(); // Marca o final do processamento em lote
        long batchProcessingTime = batchEndTime - batchStartTime;
        logger.info("Tempo de processamento em lote: {} ms", batchProcessingTime);
    }
}
