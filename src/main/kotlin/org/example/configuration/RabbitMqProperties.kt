package org.example.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.time.Duration

/**
 * Represents RabbitMQ configuration properties.
 */
@Validated
@ConfigurationProperties(prefix = "rabbitmq")
data class RabbitMqProperties @ConstructorBinding constructor(
    val addresses: String,
    val username: String,
    val password: String,
    val virtualHost: String,
    val connectionTimeout: Duration
)