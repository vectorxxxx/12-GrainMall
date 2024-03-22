package xyz.funnyboy.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.to.seckill.SeckillOrderTO;
import xyz.funnyboy.gulimall.order.config.MyRabbitMQConfig;
import xyz.funnyboy.gulimall.order.service.OrderService;

import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @description 订单关闭监听类
 * @date 2024-03-19 14:15:46
 */
@Slf4j
@Service
@RabbitListener(queues = {MyRabbitMQConfig.SECKILL_QUEUE})
public class OrderSeckillListener
{
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTO seckillOrder, Channel channel, Message message) throws IOException {
        log.info("准备创建秒杀单的详细信息：" + seckillOrder);
        final long deliveryTag = message
                .getMessageProperties()
                .getDeliveryTag();
        try {
            orderService.createSeckillOrder(seckillOrder);
            channel.basicAck(deliveryTag, false);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            // 若执行出现异常，重新放回队列中
            channel.basicReject(deliveryTag, true);
        }
    }
}
