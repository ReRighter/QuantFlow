package edu.zhou.quantflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("edu.zhou.quantflow.mapper")
@EnableScheduling
public class QuantFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantFlowApplication.class, args);
    }

}
