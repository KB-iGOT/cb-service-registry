package com.igot.service_locator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class BeanConfigurationTest {

    BeanConfiguration config;

    @BeforeEach
    void setUp() throws Exception {
        config = new BeanConfiguration();
        setField(config, "redisHost", "localhost");
        setField(config, "redisPort", 6379);
        setField(config, "redisTimeout", 5000L);
    }

    @Test
    void testRestTemplate() {
        RestTemplate restTemplate = config.restTemplate();
        assertNotNull(restTemplate, "RestTemplate should not be null");

        ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
        assertNotNull(factory, "RequestFactory should not be null");
        assertTrue(factory instanceof HttpComponentsClientHttpRequestFactory,
                "RestTemplate should use HttpComponentsClientHttpRequestFactory");
    }

    @Test
    void testRedisConnectionFactory() {
        RedisConnectionFactory factory = config.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory);

        LettuceConnectionFactory lettuceFactory = (LettuceConnectionFactory) factory;
        assertEquals("localhost", lettuceFactory.getStandaloneConfiguration().getHostName());
        assertEquals(6379, lettuceFactory.getStandaloneConfiguration().getPort());
    }

    @Test
    void testRedisTemplate() {
        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);

        assertNotNull(template);
        assertEquals(factory, template.getConnectionFactory());
        assertNotNull(template.getKeySerializer());
        assertNotNull(template.getValueSerializer());
        assertNotNull(template.getHashKeySerializer());
        assertNotNull(template.getHashValueSerializer());
    }

    // Utility to set private fields
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
