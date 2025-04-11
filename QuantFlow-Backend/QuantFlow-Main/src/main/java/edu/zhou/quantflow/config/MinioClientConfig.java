package edu.zhou.quantflow.config;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Righter
 * @Description
 * @Date since 3/11/2025
 */
@Configuration
public class MinioClientConfig {
    @Value("${minio.endpoint}")
    String endpoint;
    @Value("${minio.accessKey}")
    String accessKey;
    @Value("${minio.securityKey}")
    String secKey;

    @Bean
    public MinioClient getClient(){
        return new MinioClient.Builder()
                .endpoint(endpoint)
                .credentials(accessKey,secKey)
                .build();
    }


}
