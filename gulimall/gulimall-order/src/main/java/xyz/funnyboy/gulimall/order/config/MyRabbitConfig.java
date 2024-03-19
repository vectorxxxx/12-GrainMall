package xyz.funnyboy.gulimall.order.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-10 13:12:19
 */
@Configuration
public class MyRabbitConfig
{
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate(rabbitTemplate);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        // 使用json序列化器来序列化消息，发送消息时，消息对象会被序列化成json格式
        return new Jackson2JsonMessageConverter();
    }

    public void initRabbitTemplate(RabbitTemplate rabbitTemplate) {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> System.out.println(
                "publish -> broker 投递结果: correlationData --> " + correlationData + "\tack -->  " + ack + "\t cause -->： " + cause));
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> System.out.println(
                "Message[" + message + "] Reply Code[" + replyCode + "] Reply Text[" + replyText + "] " + "exchange[" + exchange + "] routingKey[" + routingKey + "]"));
    }
}
