package org.rogarithm.presize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PresizeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresizeApplication.class, args);
	}

}
