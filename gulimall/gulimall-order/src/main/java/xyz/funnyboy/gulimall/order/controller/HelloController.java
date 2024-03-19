package xyz.funnyboy.gulimall.order.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.funnyboy.gulimall.order.config.MyRabbitMQConfig;

import java.util.Date;
import java.util.UUID;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 10:50:48
 */
@Controller
public class HelloController
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/testSendMQ")
    @ResponseBody
    public String sendMQ() {
        rabbitTemplate.convertAndSend(
                // exchange
                MyRabbitMQConfig.EXCHANGE,
                // routingKey
                MyRabbitMQConfig.CREATE_ROUTING_KEY, UUID
                        .randomUUID()
                        .toString());
        System.out.println(new Date() + "消息发送成功，当前时间");
        return "ok";
    }

    @GetMapping("/{page}.html")
    public String hello(
            @PathVariable("page")
                    String page) {
        return page;
    }
}
