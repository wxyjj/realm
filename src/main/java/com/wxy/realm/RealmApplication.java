package com.wxy.realm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@MapperScan({"com.wxy.realm.mapper"})
@EntityScan(basePackages = "com.wxy.realm.entity")
public class RealmApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealmApplication.class, args);
    }

}

