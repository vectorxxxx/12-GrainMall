package xyz.funnyboy.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
public class MyRabbitMQConfig
{
    public static final String EXCHANGE = "order-event-exchange";

    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String RELEASE_QUEUE = "order.release.order.queue";
    public static final String RELEASE_STOCK_QUEUE = "stock.release.stock.queue";
    public static final String SECKILL_QUEUE = "order.seckill.order.queue";

    public static final String CREATE_ROUTING_KEY = "order.create.order";
    public static final String RELEASE_ROUTING_KEY = "order.release.order";
    public static final String RELEASE_OTHER_ROUTING_KEY = "order.release.other.#";
    public static final String SECKILL_ROUTING_KEY = "order.seckill.order";

    // @RabbitListener(queues = {RELEASE_QUEUE})
    // public void Listener(Channel channel, Message message) throws IOException {
    //     channel.basicAck(message
    //             .getMessageProperties()
    //             .getDeliveryTag(), false);
    //     System.out.println(new Date() + "从队列中接收到过期消息...");
    // }

    /**
     * 交换机（死信路由）
     *
     * @return {@link Exchange}
     */
    @Bean
    public Exchange OrderEventExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    /**
     * 延迟队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue OrderDelayQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", EXCHANGE);
        map.put("x-dead-letter-routing-key", RELEASE_ROUTING_KEY);
        map.put("x-message-ttl", 60000);
        return new Queue(DELAY_QUEUE, true, false, false, map);
    }

    /**
     * 死信队列
     *
     * @return {@link Queue}
     */
    @Bean
    public Queue OrderReleaseQueue() {
        return new Queue(RELEASE_QUEUE, true, false, false);
    }

    /**
     * 绑定：交换机与订单解锁延迟队列
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding OrderCreateBinding() {
        return new Binding(DELAY_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, CREATE_ROUTING_KEY, null);
    }

    /**
     * 绑定：交换机与订单解锁死信队列
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding OrderReleaseBinding() {
        return new Binding(RELEASE_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, RELEASE_ROUTING_KEY, null);
    }

    /**
     * 绑定：交换机与库存解锁
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding OrderReleaseOtherBinding() {
        return new Binding(RELEASE_STOCK_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, RELEASE_OTHER_ROUTING_KEY, null);
    }

    /**
     * 秒杀订单队列（削峰）
     */
    @Bean
    public Queue orderSecKillOrderQueue() {
        return new Queue(SECKILL_QUEUE, true, false, false);
    }

    /**
     * 绑定：交换机与秒杀订单
     */
    @Bean
    public Binding orderSecKillOrderQueueBinding() {
        return new Binding(SECKILL_QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, SECKILL_ROUTING_KEY, null);
    }

}
