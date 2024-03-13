package br.com.customeraccountintegration.camel.adapter.integration;

import br.com.customeraccountintegration.camel.adapter.api.CustomerApiHealthService;
import br.com.customeraccountintegration.camel.domain.CustomerService;
import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountSyncRouteBuilder extends RouteBuilder {

  private static final Logger logger = LoggerFactory.getLogger(CustomerAccountSyncRouteBuilder.class);

  private final CustomerService customerService;
  private final CustomerApiHealthService apiHealthService;

  @Override
  public void configure() throws Exception {
    from("timer://updateCustomerStatusTimer?fixedRate=true&period=10000") // Executar a cada 10 segundos
        .to("direct:updateCustomerStatus");

    from("direct:updateCustomerStatus")
        .process(exchange -> {
          CustomerStatusEnum statusToProcessing = CustomerStatusEnum.PROCESSING;
          long startTime = System.currentTimeMillis();

          long numberOfCustomers = customerService.countByStatus(statusToProcessing);
          if (numberOfCustomers > 0) {
            logger.info("Início da montagem da lista de clientes para processar.");
            List<CustomerModel> processingCustomers = customerService.findByStatus(statusToProcessing);
            exchange.getIn().setBody(processingCustomers);

            // Adiciona o customer atualizado a lista de atualização no banco
            for (CustomerModel customer : processingCustomers) {
              exchange.setProperty("customer", apiHealthService.syncCustomer(customer));
            }
            logger.info("Fim da montagem da lista de clientes para processar. Total de clientes: {}", processingCustomers.size());

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            logger.info("Tempo de processamento: {} milissegundos");


          }
        })
        .choice()
        .when(body().isNotNull())  // Verifica se a lista não é nula
        .split(body())
        .streaming()
        .process(exchange -> {
          CustomerModel customer = exchange.getIn().getBody(CustomerModel.class);
          customerService.updateCustomer(customer.getId(), customer);
        })
        .endChoice()
        .otherwise()
        .log("Lista de clientes vazia ou nula. Nenhuma atualização de status será realizada.")
        .end();
  }

  private CustomerStatusEnum determineNewStatus(HttpStatus apiStatusCode) {
    return apiStatusCode == HttpStatus.OK ? CustomerStatusEnum.SUCCESSFUL_PROCESSING : CustomerStatusEnum.PROCESSING_FAILURE;
  }
}
