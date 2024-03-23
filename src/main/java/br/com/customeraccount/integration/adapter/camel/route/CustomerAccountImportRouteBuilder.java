package br.com.customeraccount.integration.adapter.camel.route;

import br.com.customeraccount.integration.adapter.camel.processor.CustomerAccountFileMoveProcessor;
import br.com.customeraccount.integration.adapter.camel.processor.CustomerAccountFileToModelProcessor;
import br.com.customeraccount.integration.adapter.camel.processor.CustomerAccountSaveBatchProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountImportRouteBuilder extends RouteBuilder {

    @Value("${file.import.input.path}")
    private String fileInputPath;
    private final CustomerAccountFileMoveProcessor fileMoveProcessor;
    private final CustomerAccountFileToModelProcessor importFileProcessor;
    private final CustomerAccountSaveBatchProcessor saveBatchProcessor;

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
                .process(importFileProcessor::process)  // Processa o arquivo de importação
                .split().body()
                .streaming()
                .process(saveBatchProcessor::process) // Processa e salva em lote
                .end()
                .to("direct:moveProcessedFile");

        // Processador para mover o arquivo após o processamento em lote
        from("direct:moveProcessedFile")
                .process(fileMoveProcessor::process); // Move o arquivo processado
    }
}
