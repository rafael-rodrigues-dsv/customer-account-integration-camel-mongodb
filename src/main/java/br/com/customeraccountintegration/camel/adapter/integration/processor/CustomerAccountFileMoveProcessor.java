package br.com.customeraccountintegration.camel.adapter.integration.processor;

import org.apache.camel.Exchange;

public interface CustomerAccountFileMoveProcessor {
    void process(Exchange exchange) throws Exception;
}
