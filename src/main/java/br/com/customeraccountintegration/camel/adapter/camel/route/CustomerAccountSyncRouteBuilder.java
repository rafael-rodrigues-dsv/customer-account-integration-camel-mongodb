package br.com.customeraccountintegration.camel.adapter.camel.route;

import br.com.customeraccountintegration.camel.domain.CustomerService;
import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import br.com.customeraccountintegration.camel.domain.model.CustomerModel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CustomerAccountSyncRouteBuilder extends RouteBuilder {

  private static final Logger logger = LoggerFactory.getLogger(CustomerAccountSyncRouteBuilder.class);

  @Autowired
  private CustomerService customerService;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${api.health.url}")
  private String apiUrl;

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
            logger.info("Fim da montagem da lista de clientes para processar. Total de clientes: {}", processingCustomers.size());

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            logger.info("Tempo de processamento: {} milissegundos", elapsedTime);

            // Adiciona o status code à propriedade do Exchange
            exchange.setProperty("apiStatusCode", callApi(apiUrl));
          }
        })
        .choice()
        .when(body().isNotNull())  // Verifica se a lista não é nula
        .split(body())
        .streaming()
        .process(exchange -> {
          CustomerModel customer = exchange.getIn().getBody(CustomerModel.class);
          HttpStatus apiStatusCode = exchange.getProperty("apiStatusCode", HttpStatus.class);
          CustomerStatusEnum newStatus = determineNewStatus(apiStatusCode);
          customerService.updateStatus(customer.getId(), newStatus);
        })
        .endChoice()
        .otherwise()
        .log("Lista de clientes vazia ou nula. Nenhuma atualização de status será realizada.")
        .end();
  }

  private HttpStatus callApi(String apiUrl) {
    try {
      ResponseEntity<Void> responseEntity = restTemplate.getForEntity(apiUrl, Void.class);
      return (HttpStatus) responseEntity.getStatusCode();
    } catch (Exception e) {
      logger.error("Erro ao chamar a API: {}", e.getMessage(), e);
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  private CustomerStatusEnum determineNewStatus(HttpStatus apiStatusCode) {
    return apiStatusCode == HttpStatus.OK ? CustomerStatusEnum.SUCCESSFUL_PROCESSING : CustomerStatusEnum.PROCESSING_FAILURE;
  }
}
