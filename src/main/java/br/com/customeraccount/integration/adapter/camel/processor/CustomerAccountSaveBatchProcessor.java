package br.com.customeraccount.integration.adapter.camel.processor;

import org.apache.camel.Exchange;

public interface CustomerAccountSaveBatchProcessor {
    void process(Exchange exchange) throws Exception;
}
