package ru.sovcombank.rbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;


@SpringBootApplication
@EnableConfigurationProperties
public class TestCommonApp {
    public static void main(String[] args) {
        SpringApplication.run(TestCommonApp.class, args);
    }
}
