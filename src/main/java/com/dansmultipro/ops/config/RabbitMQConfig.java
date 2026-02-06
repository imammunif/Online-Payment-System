package com.dansmultipro.ops.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_EX_TRANSACTION = "email.notification.exchange.transaction";
    public static final String EMAIL_QUEUE_TRANSACTION = "email.notification.queue.transaction";
    public static final String EMAIL_KEY_TRANSACTION = "email.notification.key.transaction";

    public static final String EMAIL_EX_STATUS = "email.notification.exchange.status";
    public static final String EMAIL_QUEUE_STATUS = "email.notification.queue.status";
    public static final String EMAIL_KEY_STATUS = "email.notification.key.status";

    public static final String EMAIL_EX_USER = "email.notification.exchange.user";
    public static final String EMAIL_QUEUE_USER = "email.notification.queue.user";
    public static final String EMAIL_KEY_USER = "email.notification.key.user";

    @Bean
    public MessageConverter jsonUserConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange emailExchangeTransaction() {
        return new DirectExchange(EMAIL_EX_TRANSACTION);
    }

    @Bean
    public DirectExchange emailExchangeStatus() {
        return new DirectExchange(EMAIL_EX_STATUS);
    }

    @Bean
    public DirectExchange emailExchangeUser() {
        return new DirectExchange(EMAIL_EX_USER);
    }

    @Bean
    public Queue emailQueueTransaction() {
        return QueueBuilder.durable(EMAIL_QUEUE_TRANSACTION).build();
    }

    @Bean
    public Queue emailQueueStatus() {
        return QueueBuilder.durable(EMAIL_QUEUE_STATUS).build();
    }

    @Bean
    public Queue emailQueueUser() {
        return QueueBuilder.durable(EMAIL_QUEUE_USER).build();
    }

    @Bean
    public Binding categoryBindingTransaction() {
        return BindingBuilder.bind(emailQueueTransaction())
                .to(emailExchangeTransaction())
                .with(EMAIL_KEY_TRANSACTION);
    }

    @Bean
    public Binding categoryBindingStatus() {
        return BindingBuilder.bind(emailQueueStatus())
                .to(emailExchangeStatus())
                .with(EMAIL_KEY_STATUS);
    }

    @Bean
    public Binding categoryBindingUser() {
        return BindingBuilder.bind(emailQueueUser())
                .to(emailExchangeUser())
                .with(EMAIL_KEY_USER);
    }

}