package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.config.GlobalChannelInterceptorProcessor;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class ShoppingApplication {
    public static void main(String[] args) {

        SpringApplication.run(ShoppingApplication.class, args);

    }
}