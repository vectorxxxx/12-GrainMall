package xyz.funnyboy.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.funnyboy.common.to.mq.StockLockedTO;
import xyz.funnyboy.gulimall.ware.config.MyRabbitConfig;
import xyz.funnyboy.gulimall.ware.service.WareSkuService;

import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-03-19 10:48:14
 */
@Slf4j
@Service
@RabbitListener(queues = {MyRabbitConfig.RELEASE_QUEUE})
public class StockReleaseListener
{
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handlerStockLockedRelease(StockLockedTO stockLockedTO, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息......");
        final long deliveryTag = message
                .getMessageProperties()
                .getDeliveryTag();
        try {
            wareSkuService.unlockStock(stockLockedTO);
            channel.basicAck(
                    // long deliveryTag
                    deliveryTag,
                    // boolean multiple
                    false);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            // 若执行出现异常，重新放回队列中
            channel.basicReject(deliveryTag, false);
        }
    }
}
