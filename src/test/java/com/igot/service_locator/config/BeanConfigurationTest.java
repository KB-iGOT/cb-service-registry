package com.igot.service_locator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
    void testGetRestTemplate() {
        RestTemplate restTemplate = config.getRestTemplate();
        assertNotNull(restTemplate);
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
