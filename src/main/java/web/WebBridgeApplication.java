package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "web", "client", "server", "database" })
public class WebBridgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebBridgeApplication.class, args);
        System.out.println("EthioTube Web Bridge is running on http://10.198.73.78:8081");
    }
}
