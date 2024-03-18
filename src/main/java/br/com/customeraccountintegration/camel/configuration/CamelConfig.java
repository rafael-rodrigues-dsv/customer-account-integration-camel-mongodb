package br.com.customeraccountintegration.camel.configuration;

import br.com.customeraccountintegration.camel.adapter.integration.route.CustomerAccountImportRouteBuilder;
import br.com.customeraccountintegration.camel.adapter.integration.route.CustomerAccountSyncRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

  @Autowired
  private CustomerAccountImportRouteBuilder customerImportRouteBuilder;

  @Autowired
  private CustomerAccountSyncRouteBuilder customerSyncRouteBuilder;

  @Bean(destroyMethod = "stop")
  public CamelContext camelContext() throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(customerImportRouteBuilder);
    camelContext.addRoutes(customerSyncRouteBuilder);
    camelContext.start();  // Iniciar o CamelContext antes do ProducerTemplate
    return camelContext;
  }

  @Bean
  public ProducerTemplate producerTemplate(CamelContext camelContext) {
    DefaultProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
    producerTemplate.start();  // Iniciar o ProducerTemplate após o CamelContext
    return producerTemplate;
  }
}
