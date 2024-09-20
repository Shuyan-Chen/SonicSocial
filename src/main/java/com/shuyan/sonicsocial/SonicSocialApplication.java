package com.shuyan.sonicsocial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableCaching
@Transactional
public class SonicSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonicSocialApplication.class, args);
    }

}
