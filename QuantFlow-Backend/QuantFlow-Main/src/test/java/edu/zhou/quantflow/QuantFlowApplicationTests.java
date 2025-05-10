package edu.zhou.quantflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;

//@SpringBootTest
class QuantFlowApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void jsonTest()throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        LocalDateTime time = LocalDateTime.now();
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String s= objectMapper.writeValueAsString(time);
        System.out.println(s);
    }


}
