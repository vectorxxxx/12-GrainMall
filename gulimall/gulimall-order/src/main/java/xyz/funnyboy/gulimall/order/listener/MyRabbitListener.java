// package xyz.funnyboy.gulimall.order.listener;
//
// import com.rabbitmq.client.Channel;
// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
// import xyz.funnyboy.gulimall.order.entity.OrderEntity;
//
// /**
//  * @author VectorX
//  * @version V1.0
//  * @date 2024-03-11 09:00:12
//  */
// @Component
// public class MyRabbitListener
// {
//     @RabbitListener(queues = {"direct-java-queue"})
//     public void testRabbitListener(Message message, OrderEntity orderEntity, Channel channel) {
//         System.out.println("原生消息：" + message);
//         System.out.println("实体信息：" + orderEntity);
//         System.out.println("===");
//     }
// }
