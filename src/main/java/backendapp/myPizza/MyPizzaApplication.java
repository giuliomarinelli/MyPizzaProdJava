package backendapp.myPizza;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@SpringBootApplication
@EnableAsync
public class MyPizzaApplication {





	public static void main(String[] args) {
		WebClient webClient = WebClient.create("http://localhost:3500");

		SpringApplication.run(MyPizzaApplication.class, args);
	}



}
