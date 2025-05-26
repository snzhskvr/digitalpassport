package com.digitalpassport;

import org.springframework.boot.SpringApplication;

public class TestDigitalPassportApplication {

	public static void main(String[] args) {
		SpringApplication.from(DigitalPassportApplication::main).withAdditionalProfiles("test").run(args);
	}

}
