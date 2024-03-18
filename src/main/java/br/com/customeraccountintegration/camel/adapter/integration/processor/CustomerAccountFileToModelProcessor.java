package br.com.customeraccountintegration.camel.adapter.integration.processor;

import org.apache.camel.Exchange;

public interface CustomerAccountFileToModelProcessor {
    void process(Exchange exchange) throws Exception;
}
