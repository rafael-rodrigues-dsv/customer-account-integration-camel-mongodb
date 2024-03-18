package br.com.customeraccountintegration.camel.adapter.integration.processor.impl;


import br.com.customeraccountintegration.camel.adapter.integration.processor.CustomerAccountFileMoveProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountFileMoveProcessorImpl implements CustomerAccountFileMoveProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAccountFileMoveProcessorImpl.class);

    @Value("${file.import.output.path}")
    private String fileOutputPath;

    @Override
    public void process(Exchange exchange) throws Exception {
        String filePath = exchange.getIn().getHeader("CamelFileAbsolutePath", String.class);
        Path destinationPath = Paths.get(fileOutputPath, Paths.get(filePath).getFileName().toString());
        Files.move(Paths.get(filePath), destinationPath);
        logger.info("Arquivo movido para: {}", destinationPath);
    }
}