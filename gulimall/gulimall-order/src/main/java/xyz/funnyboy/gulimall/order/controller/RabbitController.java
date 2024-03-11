package xyz.funnyboy.gulimall.order.controller;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.gulimall.order.entity.OrderEntity;

import java.util.UUID;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 09:19:15
 */
@RestController
public class RabbitController
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMQ")
    public String sendMQ(
            @RequestParam(value = "num",
                          required = false,
                          defaultValue = "10")
                    Integer num) {
        OrderEntity entity = new OrderEntity();
        for (int i = 0; i < num; i++) {
            entity.setReceiverName("FIRE-" + i);
            rabbitTemplate.convertAndSend(
                    // exchange
                    "direct-java-exchange",
                    // routingKey
                    "direct-java-routing-key",
                    // object,
                    entity,
                    // CorrelationData：给信息加上唯一id（以后可以通过这个判断消息是否投递成功）
                    new CorrelationData(UUID
                            .randomUUID()
                            .toString()));
            System.out.println("消息 --> " + i + " 发送完成");
        }
        return "ok";
    }
}
