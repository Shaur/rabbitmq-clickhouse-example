package org.example.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.example.constants.ENTITY_EXCHANGE
import org.example.constants.USER_ROUTING_KEY
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.MessagePropertiesBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.Date

abstract class AbstractPublishService(private val rabbitTemplate: RabbitTemplate) {

    private val mapper = jacksonObjectMapper()

    private fun write(message: Any): Message {
        val messageProperties = MessagePropertiesBuilder.newInstance()
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setTimestamp(Date())
            .build()
        val body = mapper.writeValueAsBytes(message)
        return Message(body, messageProperties)
    }

    protected fun Any.send() {
        rabbitTemplate.send(ENTITY_EXCHANGE, routingKey(), write(this))
    }

    abstract fun routingKey(): String
}