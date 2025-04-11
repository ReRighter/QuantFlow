package edu.zhou.quantflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author Righter
 * @Description
 * @Date since 3/19/2025
 */
@Configuration
public class JedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.password}")
    private String password;


    @Bean
    public JedisPool getJedisPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setJmxEnabled(false);
        return new JedisPool(poolConfig,host,6379,5000,password);
    }
}
