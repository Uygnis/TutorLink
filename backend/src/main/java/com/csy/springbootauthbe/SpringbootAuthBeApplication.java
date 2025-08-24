package com.csy.springbootauthbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SpringbootAuthBeApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringbootAuthBeApplication.class, args);
	}
}