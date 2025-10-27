package com.csy.springbootauthbe.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@ConditionalOnProperty(name = "app.mongo.enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoAuditing
public class MongoConfig {
}
