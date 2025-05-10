package edu.zhou.quantflow;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * @Author Righter
 * @Description
 * @Date since 4/11/2025
 */
/*@EnableRedisHttpSession*/ //使用servlet
@EnableRedisWebSession  //将会话信息储存到redis中;web为响应式,使用webflux实现
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        //启动网关
        org.springframework.boot.SpringApplication.run(GatewayApplication.class, args);
    }
}
