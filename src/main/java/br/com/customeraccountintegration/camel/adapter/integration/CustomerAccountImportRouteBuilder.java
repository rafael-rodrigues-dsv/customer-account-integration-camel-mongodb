package br.com.customeraccountintegration.camel.adapter.integration;

import br.com.customeraccountintegration.camel.domain.CustomerService;
import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class CustomerAccountImportRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAccountImportRouteBuilder.class);
    private final CustomerService customerService;

    @Value("${file.import.input.path}")
    private String fileInputPath;

    @Value("${file.import.output.path}")
    private String fileOutputPath;

    @Override
    public void configure() throws Exception {
        // Rota do tópico para iniciar o processamento
        from("direct:startFileProcessing")
            .to("file:" + fileInputPath + "?noop=true&idempotent=false&include=.*\\.txt");

        // Rota principal que escuta o tópico e processa os arquivos
        from("file:" + fileInputPath + "?noop=true&idempotent=false&include=.*\\.txt")
            .onException(Exception.class)
            .log("Erro durante o processamento do arquivo: ${exception.message}")
            .handled(true)
            .end()
            .process(exchange -> {
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
            })
            .split().body()
            .streaming()
            .process(exchange -> {
                long batchStartTime = System.currentTimeMillis(); // Marca o início do processamento em lote
                List<CustomerModel> customerList = exchange.getProperty("customerList", List.class);

                if (customerList != null && !customerList.isEmpty()) {
                    logger.info("Salvando em lote - Total de registros: {}", customerList.size());
                    customerService.addBatch(customerList);
                }

                long batchEndTime = System.currentTimeMillis(); // Marca o final do processamento em lote
                long batchProcessingTime = batchEndTime - batchStartTime;
                logger.info("Tempo de processamento em lote: {} ms", batchProcessingTime);
            })
            .end()
            .to("direct:moveProcessedFile");

        // Processador para mover o arquivo após o processamento em lote
        from("direct:moveProcessedFile")
            .process(exchange -> {
                // Move o arquivo para o diretório de saída
                String filePath = exchange.getIn().getHeader("CamelFileAbsolutePath", String.class);
                Path destinationPath = Paths.get(fileOutputPath, Paths.get(filePath).getFileName().toString());
                Files.move(Paths.get(filePath), destinationPath);
                logger.info("Arquivo movido para: {}", destinationPath);
            });

        // Gatilho para iniciar o processamento
        from("direct:triggerFileProcessing")
            .to("direct:startFileProcessing");
    }
}
