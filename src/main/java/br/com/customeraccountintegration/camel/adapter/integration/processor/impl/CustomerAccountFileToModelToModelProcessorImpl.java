package br.com.customeraccountintegration.camel.adapter.integration.processor.impl;

import br.com.customeraccountintegration.camel.adapter.integration.processor.CustomerAccountFileToModelProcessor;
import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountFileToModelToModelProcessorImpl implements CustomerAccountFileToModelProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAccountFileToModelToModelProcessorImpl.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        long startTime = System.currentTimeMillis(); // Marca o início do processamento

        Path filePath = Paths.get(exchange.getIn().getHeader("CamelFileAbsolutePath", String.class));
        try {
            List<CustomerModel> customerList = Files.lines(filePath)
                    .map(line -> Arrays.asList(line.split("\\|")).stream()
                            .map(column -> column.split(";"))
                            .filter(columns -> columns.length == 3)
                            .map(columns -> CustomerModel.builder()
                                    .documentNumber(columns[0].trim())
                                    .email(columns[1].trim())
                                    .contract(columns[2].trim())
                                    .customerStatus(CustomerStatusEnum.IMPORTED)
                                    .build())
                            .collect(Collectors.toList()))
                    .collect(ArrayList::new, List::addAll, List::addAll);

            if (!customerList.isEmpty()) {
                logger.info("Montada lista - Total de registros: {}", customerList.size());
                exchange.setProperty("customerList", customerList);
            }
        } catch (Exception e) {
            logger.error("Erro durante o processamento do arquivo", e);
            throw e;
        }

        long endTime = System.currentTimeMillis(); // Marca o final do processamento
        long processingTime = endTime - startTime;
        logger.info("Tempo de processamento: {} ms", processingTime);
    }
}
