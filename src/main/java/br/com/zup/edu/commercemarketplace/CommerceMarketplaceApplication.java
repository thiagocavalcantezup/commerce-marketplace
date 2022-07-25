package br.com.zup.edu.commercemarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CommerceMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceMarketplaceApplication.class, args);
    }

}
