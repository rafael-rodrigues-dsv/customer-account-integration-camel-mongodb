package br.com.customeraccount.integration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Generated
@OpenAPIDefinition(info = @Info(title = "Customer Account Integration Api", version = "1.0"))
@SpringBootApplication(scanBasePackages = {"br.com.customeraccount.integration"})
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
