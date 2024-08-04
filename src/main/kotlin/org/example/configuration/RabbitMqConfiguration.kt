package org.example.configuration

import org.example.utils.RabbitMqConfigurationUtils
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy


/**
 * The configuration of rabbit
 */
@Configuration
@EnableConfigurationProperties(RabbitMqProperties::class)
class RabbitMqConfiguration(private val properties: RabbitMqProperties) {

    @Bean
    fun rabbitMqTemplate(connectionFactory: CachingConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory)
    }

    @Bean
    fun connectionFactory(): CachingConnectionFactory {
        return RabbitMqConfigurationUtils.configureConnectionFactory(properties)
    }

    @Lazy
    @Bean
    fun rabbitMqAdmin(connectionFactory: CachingConnectionFactory): RabbitAdmin {
        return RabbitMqConfigurationUtils.configureAdmin(connectionFactory)
    }
}