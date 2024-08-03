package org.example.service

import org.example.org.example.constants.TRANSFER_ROUTING_KEY
import org.example.org.example.dto.CreateTransferDto
import org.example.org.example.model.Transfer
import org.example.org.example.service.AbstractPublishService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class TransferService(rabbitTemplate: RabbitTemplate) : AbstractPublishService(rabbitTemplate) {

    fun create(dto: CreateTransferDto) {
        Transfer(
            customerId = dto.customerId,
            amount = dto.amount,
            currency = dto.currency
        ).send()
    }

    override fun routingKey() = TRANSFER_ROUTING_KEY

}
