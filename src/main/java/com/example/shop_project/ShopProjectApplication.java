package com.example.shop_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class ShopProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopProjectApplication.class, args);
    }
}
