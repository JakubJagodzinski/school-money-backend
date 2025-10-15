package com.example.schoolmoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SchoolMoneyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolMoneyApplication.class, args);
    }

}
