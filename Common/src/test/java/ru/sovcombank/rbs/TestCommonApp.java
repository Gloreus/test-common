package ru.sovcombank.rbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class TestCommonApp {
    public static void main(String[] args) {
        SpringApplication.run(TestCommonApp.class, args);
    }
}
