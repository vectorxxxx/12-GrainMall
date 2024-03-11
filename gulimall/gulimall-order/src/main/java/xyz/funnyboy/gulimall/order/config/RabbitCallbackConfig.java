package xyz.funnyboy.gulimall.order.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-11 09:15:36
 */
@Configuration
public class RabbitCallbackConfig
{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * PostConstruct作用：MyRabbitConfig对象创建完成后，执行这个方法。
     */
    @PostConstruct
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> System.out.println(
                "publish -> broker 投递结果: correlationData --> " + correlationData + "\tack -->  " + ack + "\t cause -->： " + cause));
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> System.out.println(
                "Message[" + message + "] Reply Code[" + replyCode + "] Reply Text[" + replyText + "] " + "exchange[" + exchange + "] routingKey[" + routingKey + "]"));
    }
}
