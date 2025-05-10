package edu.zhou.quantflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/8/2025
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {
    private List<String> allowedOrigins; //从配置文件中读取允许的源
}
