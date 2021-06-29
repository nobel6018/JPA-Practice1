package com.cloudy.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

    // Entity를 직접 반환할 때 생기는 문제해결하기 위해서 (실무에서는 DTO 반환하기 때문에 안 쓴다)
    // LAZY 로딩을 해서 프록시가 초기화된 즉, 데이터가 로딩된 애들만 반환된다
    @Bean
    Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
}
