package com.solar.monitoring.system.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    /**
     * Creates a Lettuce-based RedisConnectionFactory for a standalone Redis instance.
     *
     * <p>Uses the configured host, port, and password properties to build a
     * RedisStandaloneConfiguration and returns a LettuceConnectionFactory initialized
     * with that configuration.
     *
     * @return a RedisConnectionFactory backed by Lettuce for the configured standalone Redis
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    /**
     * Creates and configures a RedisTemplate for String keys and JSON-serialized values.
     *
     * <p>Configures the template to use the application's RedisConnectionFactory, String serialization
     * for keys and hash keys, and GenericJackson2JsonRedisSerializer for values, hash values, and as
     * the default serializer. The template is fully initialized via {@code afterPropertiesSet()}.
     *
     * @return a ready-to-use {@code RedisTemplate<String, Object>} configured with string key
     *         serialization and JSON value serialization
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }
}