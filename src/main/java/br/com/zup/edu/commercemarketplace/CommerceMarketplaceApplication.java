package br.com.zup.edu.commercemarketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@SpringBootApplication
@EnableKafka
@EnableAsync
public class CommerceMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceMarketplaceApplication.class, args);
    }

}
