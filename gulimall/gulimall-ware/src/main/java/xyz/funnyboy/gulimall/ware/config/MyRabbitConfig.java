package xyz.funnyboy.gulimall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public static final String EXCHANGE = "stock-event-exchange";
    public static final String RELEASE_QUEUE = "stock-release-queue";
    public static final String DELAY_QUEUE = "stock-delay-queue";
    public static final String RELEASE_ROUTING_KEY = "stock.release.#";
    public static final String LOCKED_ROUTING_KEY = "stock.locked";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // @RabbitListener(queues = {RELEASE_QUEUE})
    // public void listener(Channel channel, Message message) {
    // }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", EXCHANGE);
        map.put("x-dead-letter-routing-key", RELEASE_ROUTING_KEY);
        map.put("x-message-ttl", 30000);
        return new Queue(DELAY_QUEUE, true, false, false, map);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue(RELEASE_QUEUE, true, false, false);
    }

    @Bean
    public Binding stockLocked() {
        return new Binding(DELAY_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, LOCKED_ROUTING_KEY, null);
    }

    @Bean
    public Binding stockRelease() {
        return new Binding(RELEASE_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, RELEASE_ROUTING_KEY, null);
    }
}
