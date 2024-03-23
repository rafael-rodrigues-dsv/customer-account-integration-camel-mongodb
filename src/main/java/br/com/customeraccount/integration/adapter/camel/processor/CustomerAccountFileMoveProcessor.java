package br.com.customeraccount.integration.adapter.camel.processor;

import org.apache.camel.Exchange;

public interface CustomerAccountFileMoveProcessor {
    void process(Exchange exchange) throws Exception;
}
