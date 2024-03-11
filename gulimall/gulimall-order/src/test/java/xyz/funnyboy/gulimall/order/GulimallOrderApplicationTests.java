package xyz.funnyboy.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;

@SpringBootTest
@Slf4j
class GulimallOrderApplicationTests
{

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void createExchange() {
        // 1、声明一个交换机
        final DirectExchange directExchange = new DirectExchange(
                // 交换机名称
                "direct-java-exchange",
                // 持久化
                true,
                // 自动删除
                false);
        amqpAdmin.declareExchange(directExchange);

        // 2、声明一个队列
        final Queue queue = new Queue(
                // 队列名称
                "direct-java-queue",
                // 持久化
                true,
                // 唯一的
                false,
                // 自动删除
                false);
        amqpAdmin.declareQueue(queue);

        // 3、声明一个绑定
        final Binding binding = new Binding(
                // 目的地（队列名称）
                "direct-java-queue",
                // 目的地类型（队列类型）
                Binding.DestinationType.QUEUE,
                // 交换机名称
                "direct-java-exchange",
                // 路由键
                "direct-java-routing-key",
                // 自定义参数
                null);
        amqpAdmin.declareBinding(binding);

        // Attempting to connect to: [192.168.56.10:5672]
        // Created new connection: rabbitConnectionFactory#45e6d1e0:0/SimpleConnection@3554bdc0 [delegate=amqp://guest@192.168.56.10:5672/, localPort= 28380]
    }

    @Test
    public void sendMsg() {
        // 1、发送消息
        rabbitTemplate.convertAndSend("direct-java-exchange", "direct-java-routing-key", "hello this is java");
        // 2、发送对象【对象必须实现序列化】
        // rabbitTemplate.convertAndSend("direct-java-exchange", "direct-java-routing-key", new Date());
    }

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            final OrderEntity orderEntity = new OrderEntity();
            orderEntity.setReceiverPhone("123123");
            rabbitTemplate.convertAndSend("direct-java-exchange", "direct-java-routing-key", orderEntity);
            log.info("发送消息成功 -- " + i);
        }
    }
}
