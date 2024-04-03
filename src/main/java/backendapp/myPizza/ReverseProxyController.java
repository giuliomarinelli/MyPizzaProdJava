package backendapp.myPizza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@RestController
public class ReverseProxyController {

//    private final ReverseProxyHandler reverseProxyHandler;
//
//    @Autowired
//    public ReverseProxyController(ReverseProxyHandler reverseProxyHandler) {
//        this.reverseProxyHandler = reverseProxyHandler;
//    }
//
//    @GetMapping("/{path}")
//    public Mono<String> handleRequest(@RequestParam String EIO, @RequestParam String transport, @PathVariable String path) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("EIO", EIO);
//        params.put("transport", transport);
//        return reverseProxyHandler.handleRequest("/" + path, HttpMethod.GET, params);
//    }
}