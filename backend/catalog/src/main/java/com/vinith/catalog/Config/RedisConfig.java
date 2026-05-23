package com.vinith.catalog.Config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // tell Lettuce to give up fast if Redis is unreachable
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(500)) // give up connecting in 500ms
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS) // don't queue commands when disconnected
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(500)) // command must complete in 500ms
                .clientOptions(clientOptions)
                .build();

        org.springframework.data.redis.connection.RedisStandaloneConfiguration serverConfig =
                new org.springframework.data.redis.connection.RedisStandaloneConfiguration(host, port);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
}