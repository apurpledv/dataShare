package com.openclassrooms.dataShare_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.openclassrooms.dataShare_api.config.file.StorageProperties;

@EnableConfigurationProperties(StorageProperties.class)
@SpringBootApplication
public class DataShareApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataShareApiApplication.class, args);
	}

}
