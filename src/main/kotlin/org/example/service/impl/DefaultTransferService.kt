package org.example.service.impl

import org.example.constants.TRANSFER_ROUTING_KEY
import org.example.dto.CreateTransferDto
import org.example.model.Transfer
import org.example.service.AbstractPublishService
import org.example.service.TransferService
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class DefaultTransferService(rabbitTemplate: RabbitTemplate) : TransferService, AbstractPublishService(rabbitTemplate) {

    override fun create(dto: CreateTransferDto) {
        Transfer(
            customerId = dto.customerId,
            amount = dto.amount,
            currency = dto.currency
        ).send()
    }

    override fun routingKey() = TRANSFER_ROUTING_KEY

}
