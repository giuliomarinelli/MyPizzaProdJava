package backendapp.myPizza;

import com.cloudinary.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ReverseProxyHandler {

    @Autowired
    private WebClient webClient;


    public Mono<String> handleRequest(String path, HttpMethod method, Map<String, String> queryParams) {
        if (path.startsWith("/socket.io")) {
            // Forward request to Socket.io server (Netty)
            return webClient.method(method)
                    .uri("ws://localhost:8085/socket.io?EIO=" + queryParams.get("EIO") + "&transport=" + queryParams.get("transport"))
                    .retrieve()
                    .bodyToMono(String.class);

        } else {
            // Handle other paths as needed
            return Mono.just("Unsupported path");
        }
    }
}