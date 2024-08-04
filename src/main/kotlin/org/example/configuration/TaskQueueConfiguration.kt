package org.example.configuration

import org.example.utils.RabbitMqConfigurationUtils
import org.example.utils.RabbitMqConfigurationUtils.declareQueueAndExchange
import org.example.utils.RabbitMqConfigurationUtils.declareQueuesAndExchangesWithRetry
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class TaskQueueConfiguration {

    @Autowired
    fun declareBindings(rabbitMqAdmin: RabbitAdmin) {
//        declareQueuesAndExchangesWithRetry(
//            listOf("entity-queue"),
//             mainExchangeName = "entity-exchange",
//            rabbitMqAdmin,
//            autoDeclare = true,
//            messageTtl = Duration.ofSeconds(30),
//            delayedExchangeName = "entity-delayed-exchange",
//        )
//
//        val callbackExchange = FanoutExchange("entity-callback-exchange")
//        callbackExchange.setAdminsThatShouldDeclare(rabbitMqAdmin)
//        callbackExchange.setShouldDeclare(true)
//        rabbitMqAdmin.declareExchange(callbackExchange)

        declareQueueAndExchange(
            queueName = "user-queue",
            exchangeName = "entity-exchange",
            bindingKey = "user",
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = true
        )

        declareQueueAndExchange(
            queueName = "transfer-queue",
            exchangeName = "entity-exchange",
            bindingKey = "transfer",
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = true
        )
    }

}