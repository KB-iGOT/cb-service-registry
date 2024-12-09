package com.igot.service_locator.config;


import com.igot.service_locator.entity.ServiceLocatorEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.*;

@Configuration
public class BeanConfiguration {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisTemplate<String, ServiceLocatorEntity> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ServiceLocatorEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }
}
