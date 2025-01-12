package ru.otus.hw;


import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.hw.changelogs.RunnerBuilderProviderImpl;

@EnableMongock
@SpringBootApplication
//@MongockCliConfiguration(sources = RunnerBuilderProviderImpl.class )
public class Application {

	public static void main(String[] args) {
		/*new RunnerBuilderProviderImpl()
				.getBuilder()
				.buildRunner()
				.execute();*/

		//http://localhost:8080/webjars/swagger-ui/index.html
		System.out.println("http://localhost:8080/webjars/swagger-ui/index.html");
		SpringApplication.run(Application.class, args);
	}

}
