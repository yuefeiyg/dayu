package com.dayu;


import com.dayu.filter.VerifyIpFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableZuulProxy
public class ClassifyZuulServerApplication {

    public static void main( String[] args ) {
        SpringApplication.run(ClassifyZuulServerApplication.class, args);
    }


    @Bean
    public VerifyIpFilter VerifyIpFilter(){
        return new VerifyIpFilter();
    }
}
