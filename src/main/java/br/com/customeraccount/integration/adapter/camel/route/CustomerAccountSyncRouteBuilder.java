package br.com.customeraccount.integration.adapter.camel.route;

import br.com.customeraccount.integration.adapter.api.CustomerApiHealthService;
import br.com.customeraccount.integration.domain.CustomerService;
import br.com.customeraccount.integration.domain.enumeration.CustomerStatusEnum;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                    long numberOfCustomers = customerService.countByStatus(statusToProcessing);

                    if (numberOfCustomers > 0) {
                        long startTime = System.currentTimeMillis();
                        logger.info("Iniciando integração com a API para processar clientes.");

                        customerService.findByStatus(statusToProcessing)
                                .forEach(customer -> {
                                    apiHealthService.syncCustomer(customer);
                                    customerService.updateCustomer(customer.getId(), customer);
                                });

                        long endTime = System.currentTimeMillis();
                        long elapsedTime = endTime - startTime;
                        logger.info("Concluída integração com a API para processar clientes. Tempo de processamento: {} milissegundos", elapsedTime);
                    }
                });
    }
}
