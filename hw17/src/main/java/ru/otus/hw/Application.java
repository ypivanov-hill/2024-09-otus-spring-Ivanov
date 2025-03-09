package ru.otus.hw;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
public class Application {

	public static void main(String[] args) {

		//http://localhost:8080/swagger-ui/index.html
		SpringApplication.run(Application.class, args);
	}

}
