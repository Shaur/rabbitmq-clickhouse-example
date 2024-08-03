package org.example.org.example.service

import org.example.org.example.constants.USER_ROUTING_KEY
import org.example.org.example.dto.CreateUserDto
import org.example.org.example.dto.UpdateUserDto
import org.example.org.example.model.User
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class UserService(rabbitTemplate: RabbitTemplate) : AbstractPublishService(rabbitTemplate) {

    fun create(dto: CreateUserDto) {
        User(dto.id, dto.fullName).send()
    }

    fun update(id: Long, dto: UpdateUserDto) {
        User(id, dto.fullName).send()
    }

    override fun routingKey() = USER_ROUTING_KEY

}