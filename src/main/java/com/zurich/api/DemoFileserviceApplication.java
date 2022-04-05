package com.zurich.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;


/**
 * @author Dragisa Dragisic, 2022
 *
 */
@OpenAPIDefinition(
		info = @Info(
			title = "File-Service API",
			description = "REST-Service für Filetransfer, Upload und Download von Files und Data-Records.\n Weitere Infos:\n- [File-Transer](https://confluence.zurich.com)",
			contact = @Contact(
					name = "CoE API-Management", 
					url = "https://confluence.zurich.com", 
					email = "dragisa.dragisic@zurich.com"
			),
			license = @License(
					name = "License", 
					url = "https://confluence.zurich.com"
			),
			version = "0.0.1"
		),
		servers = {
				@Server( 
						url="http://localhost:8999",
						description="File-Service URL"
				)
		}
)
@SpringBootApplication
public class DemoFileserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoFileserviceApplication.class, args);
	}

}
