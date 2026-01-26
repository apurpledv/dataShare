package com.openclassrooms.dataShare_api;

import org.springframework.boot.SpringApplication;

public class TestDataShareApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(DataShareApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
