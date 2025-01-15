package ru.otus.hw;


import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		//http://localhost:8080/webjars/swagger-ui/index.html
		System.out.println("http://localhost:8080/webjars/swagger-ui/index.html");
		SpringApplication.run(Application.class, args);
	}

}
