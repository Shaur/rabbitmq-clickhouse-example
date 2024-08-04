package org.example.service.impl

import org.example.constants.USER_ROUTING_KEY
import org.example.dto.CreateUserDto
import org.example.dto.UpdateUserDto
import org.example.model.User
import org.example.service.AbstractPublishService
import org.example.service.UserService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class DefaultUserService(rabbitTemplate: RabbitTemplate) : UserService, AbstractPublishService(rabbitTemplate) {

    override fun create(dto: CreateUserDto) {
        User(dto.id, dto.fullName).send()
    }

    override fun update(id: Long, dto: UpdateUserDto) {
        User(id, dto.fullName).send()
    }

    override fun routingKey() = USER_ROUTING_KEY

}