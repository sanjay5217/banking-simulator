package com.sanjay.bank_sim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BankSimApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankSimApplication.class, args);
	}

}
