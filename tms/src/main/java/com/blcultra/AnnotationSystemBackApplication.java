package com.blcultra;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan
@MapperScan(value ={"com.blcultra.dao"})
public class AnnotationSystemBackApplication {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(AnnotationSystemBackApplication.class);
        application.run(args);
    }
}
