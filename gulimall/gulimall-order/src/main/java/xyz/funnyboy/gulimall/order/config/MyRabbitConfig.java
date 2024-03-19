package xyz.funnyboy.gulimall.order.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-10 13:12:19
 */
@Configuration
public class MyRabbitConfig
{
    public static final String EXCHANGE = "order-event-exchange";

    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String RELEASE_QUEUE = "order.release.queue";
    public static final String RELEASE_ROUTING_KEY = "order.release.routing.key";
    public static final String CREATE_ROUTING_KEY = "order.create.routing.key";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = {RELEASE_QUEUE})
    public void Listener(Channel channel, Message message) throws IOException {
        channel.basicAck(message
                .getMessageProperties()
                .getDeliveryTag(), false);
        System.out.println(new Date() + "从队列中接收到过期消息...");
    }

    @Bean
    public Exchange OrderEventExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue OrderDelayQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", EXCHANGE);
        map.put("x-dead-letter-routing-key", RELEASE_ROUTING_KEY);
        map.put("x-message-ttl", 30000);
        return new Queue(DELAY_QUEUE, true, false, false, map);
    }

    @Bean
    public Queue OrderReleaseQueue() {
        return new Queue(RELEASE_QUEUE, true, false, false);
    }

    @Bean
    public Binding OrderCreateBinding() {
        return new Binding(DELAY_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, CREATE_ROUTING_KEY, null);
    }

    @Bean
    public Binding OrderReleaseBinding() {
        return new Binding(RELEASE_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, RELEASE_ROUTING_KEY, null);
    }
}
