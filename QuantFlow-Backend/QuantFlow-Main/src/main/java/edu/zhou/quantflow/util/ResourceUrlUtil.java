package edu.zhou.quantflow.util;

import edu.zhou.quantflow.entity.Resource;
import edu.zhou.quantflow.mapper.ResourceMapper;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author Righter
 * @Description
 * @Date since 3/23/2025
 */
@Component
@Log4j2
public class ResourceUrlUtil {
    private final MinioClient minioClient;
    private final ResourceMapper resourceMapper;
    private final JedisPool jedisPool;

    @Autowired
    public ResourceUrlUtil( MinioClient minioClient, ResourceMapper resourceMapper, JedisPool jedisPool) {
        this.minioClient = minioClient;
        this.resourceMapper = resourceMapper;
        this.jedisPool = jedisPool;
    }

    public String getResourceUrlById(Integer resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        String pathInMinio = resource.getPath()+resource.getName();
        return getResourceUrlByPath(pathInMinio);
    }

    public String getResourceUrlByPath(String pathInMinio)  {
        try (Jedis jedis = jedisPool.getResource()){
            String url = jedis.get(pathInMinio);
            if(url==null){
                url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket("resources")
                        .method(Method.GET).object(pathInMinio).build()) ;
                jedis.set(pathInMinio,url);
            }
            return url;
        }catch (Exception e){
            log.error("获取url失败");
            //e.printStackTrace();
            return null;
        }
    }
}
