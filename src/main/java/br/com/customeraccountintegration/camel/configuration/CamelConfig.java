package br.com.customeraccountintegration.camel.configuration;

import br.com.customeraccountintegration.camel.adapter.integration.CustomerAccountImportRouteBuilder;
import br.com.customeraccountintegration.camel.adapter.integration.CustomerAccountSyncRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class CamelConfig {

  private CamelContext camelContext;

  @Bean
  @DependsOn({"customCustomerImportRouteBuilder", "customCustomerSyncRouteBuilder"})
  public CamelContext camelContext() throws Exception {
    if (camelContext == null) {
      camelContext = new DefaultCamelContext();
      camelContext.addRoutes(customCustomerImportRouteBuilder());
      camelContext.addRoutes(customCustomerSyncRouteBuilder());
      camelContext.start();  // Iniciar o CamelContext antes do ProducerTemplate
    }

    return camelContext;
  }

  @Bean
  public ProducerTemplate producerTemplate(CamelContext camelContext) {
    DefaultProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
    producerTemplate.start();  // Iniciar o ProducerTemplate após o CamelContext
    return producerTemplate;
  }

  @Bean
  public CustomerAccountImportRouteBuilder customCustomerImportRouteBuilder() {
    return new CustomerAccountImportRouteBuilder();
  }

  @Bean
  public CustomerAccountSyncRouteBuilder customCustomerSyncRouteBuilder() {
    return new CustomerAccountSyncRouteBuilder();
  }

  @Bean(destroyMethod = "stop")
  public CamelContext camelContextShutdown() throws Exception {
    camelContext.getShutdownStrategy().setTimeout(30);  // Tempo de encerramento em segundos
    return camelContext;
  }

  @Bean
  public void configure() throws Exception {
    camelContext.addRoutes(customCustomerSyncRouteBuilder());
  }
}
