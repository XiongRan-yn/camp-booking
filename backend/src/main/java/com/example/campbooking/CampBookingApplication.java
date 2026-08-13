package com.example.campbooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.campbooking.g.mapper,com.example.campbooking.mapper")
public class CampBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampBookingApplication.class, args);
    }
}