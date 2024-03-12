package br.com.customeraccountintegration.camel;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Generated
@OpenAPIDefinition(info = @Info(title = "Customer Account Integration Api", version = "1.0"))
@SpringBootApplication(scanBasePackages = {"br.com.customeraccountintegration.camel"})
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
