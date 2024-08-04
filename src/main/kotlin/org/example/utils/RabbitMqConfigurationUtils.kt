package org.example.utils

import org.example.configuration.RabbitMqProperties
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener
import java.time.Duration

/**
 * The auxiliary class for common Rabbit MQ configuration code
 */
object RabbitMqConfigurationUtils {

    fun configureListenerContainer(
        connectionFactory: CachingConnectionFactory,
        queueName: String,
        listener: ChannelAwareMessageListener,
        prefetchCount: Int = 1,
        consumers: Int = 1
    ): MessageListenerContainer {
        val container = DirectMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.acknowledgeMode = AcknowledgeMode.MANUAL
        //WARNING: some logic (e.g. ETL-Extractor) assumes that we always read messages one-by-one
        // and implementation relies on this fact
        container.setPrefetchCount(prefetchCount)
        container.setConsumersPerQueue(consumers)
        container.setQueueNames(queueName)
        container.setMessageListener(listener)
        return container
    }

    fun declareQueueAndExchange(
        queueName: String,
        exchangeName: String,
        bindingKey: String,
        rabbitMqAdmin: RabbitAdmin,
        autoDeclare: Boolean,
        deadLetterExchange: String? = null,
        messageTtl: Duration? = null,
        parentExchange: FanoutExchange? = null
    ) {
        val exchange = DirectExchange(exchangeName)
        exchange.setAdminsThatShouldDeclare(rabbitMqAdmin)
        exchange.setShouldDeclare(autoDeclare)

        val queueBuilder = QueueBuilder.durable(queueName)
        deadLetterExchange?.let { queueBuilder.withArgument("x-dead-letter-exchange", it) }
        messageTtl?.let { queueBuilder.withArgument("x-message-ttl", it.toMillis()) }

        val queue = queueBuilder.build()
        queue.setAdminsThatShouldDeclare(rabbitMqAdmin)
        queue.setShouldDeclare(autoDeclare)

        val binding = BindingBuilder.bind(queue).to(exchange).with(bindingKey)
        rabbitMqAdmin.declareExchange(exchange)
        rabbitMqAdmin.declareQueue(queue)
        rabbitMqAdmin.declareBinding(binding)

        if (parentExchange != null) {
            val bindingExchange = BindingBuilder.bind(exchange).to(parentExchange)
            rabbitMqAdmin.declareBinding(bindingExchange)
        }
    }

    fun declareQueuesAndExchangesWithRetry(
        queueNames: List<String>,
        mainExchangeName: String,
        rabbitMqAdmin: RabbitAdmin,
        autoDeclare: Boolean,
        messageTtl: Duration,
        delayedExchangeName: String? = null
    ) {
        val mainExchange = FanoutExchange(mainExchangeName)
        mainExchange.setAdminsThatShouldDeclare(rabbitMqAdmin)
        mainExchange.setShouldDeclare(autoDeclare)
        rabbitMqAdmin.declareExchange(mainExchange)

        val delayedExchange = delayedExchangeName?.let {
            FanoutExchange(delayedExchangeName).also {
                it.setAdminsThatShouldDeclare(rabbitMqAdmin)
                it.setShouldDeclare(autoDeclare)
                rabbitMqAdmin.declareExchange(it)
            }
        }


        for (queueName in queueNames) {
            declareQueuesAndExchangesWithRetry(
                parentExchange = mainExchange,
                mainQueueName = queueName,
                mainExchangeName = "$queueName-exchange",
                delayedExchange = delayedExchange,
                retryQueueName = "$queueName-retry",
                retryExchangeName = "$queueName-exchange-retry",
                retryMessageTtl = messageTtl,
                rabbitMqAdmin = rabbitMqAdmin,
                autoDeclare = autoDeclare
            )
        }
    }

    private fun declareQueueAndExchange(
        queueName: String,
        exchangeName: String,
        rabbitMqAdmin: RabbitAdmin,
        autoDeclare: Boolean,
        deadLetterExchange: String?,
        messageTtl: Duration?,
        parentExchange: FanoutExchange? = null
    ) {
        declareQueueAndExchange(
            queueName = queueName,
            exchangeName = exchangeName,
            bindingKey = "",
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = autoDeclare,
            deadLetterExchange = deadLetterExchange,
            messageTtl = messageTtl,
            parentExchange = parentExchange
        )
    }

    fun declareQueuesAndExchangesWithRetry(
        mainQueueName: String,
        mainExchangeName: String,
        retryQueueName: String,
        retryExchangeName: String,
        retryMessageTtl: Duration,
        rabbitMqAdmin: RabbitAdmin,
        autoDeclare: Boolean
    ) {
        declareQueueAndExchange(
            queueName = mainQueueName,
            exchangeName = mainExchangeName,
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = autoDeclare,
            deadLetterExchange = retryExchangeName,
            messageTtl = null
        )
        declareQueueAndExchange(
            queueName = retryQueueName,
            exchangeName = retryExchangeName,
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = autoDeclare,
            deadLetterExchange = mainExchangeName,
            messageTtl = retryMessageTtl
        )
    }

    fun declareQueuesAndExchangesWithRetry(
        parentExchange: FanoutExchange,
        mainQueueName: String,
        mainExchangeName: String,
        retryQueueName: String,
        retryExchangeName: String,
        retryMessageTtl: Duration,
        rabbitMqAdmin: RabbitAdmin,
        autoDeclare: Boolean,
        delayedExchange: FanoutExchange? = null
    ) {
        declareQueueAndExchange(
            queueName = mainQueueName,
            exchangeName = mainExchangeName,
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = autoDeclare,
            deadLetterExchange = retryExchangeName,
            messageTtl = null,
            parentExchange = parentExchange
        )
        declareQueueAndExchange(
            queueName = retryQueueName,
            exchangeName = retryExchangeName,
            rabbitMqAdmin = rabbitMqAdmin,
            autoDeclare = autoDeclare,
            deadLetterExchange = mainExchangeName,
            messageTtl = retryMessageTtl,
            parentExchange = delayedExchange
        )
    }

    fun configureConnectionFactory(properties: RabbitMqProperties): CachingConnectionFactory {
        return CachingConnectionFactory().apply {
            setAddresses(properties.addresses)
            username = properties.username
            setPassword(properties.password)
            virtualHost = properties.virtualHost
            setConnectionTimeout(properties.connectionTimeout.toMillis().toInt())
            channelCacheSize = 1
        }
    }

    fun configureAdmin(connectionFactory: CachingConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory).apply { isAutoStartup = true }
    }
}