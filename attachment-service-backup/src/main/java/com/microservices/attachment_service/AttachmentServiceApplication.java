package com.microservices.attachment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class,
				DataSourceTransactionManagerAutoConfiguration.class
		},
		scanBasePackages = {
				"com.microservices.attachment_service",
				"com.microservices.common_service"
		}
)
public class AttachmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttachmentServiceApplication.class, args);
	}

}
