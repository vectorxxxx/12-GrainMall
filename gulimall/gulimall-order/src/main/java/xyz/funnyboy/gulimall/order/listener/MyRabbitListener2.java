// package xyz.funnyboy.gulimall.order.listener;
//
// import com.rabbitmq.client.Channel;
// import org.springframework.amqp.core.Message;
// import org.springframework.amqp.rabbit.annotation.RabbitHandler;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
// import xyz.funnyboy.gulimall.order.entity.OrderEntity;
//
// import java.io.IOException;
//
// /**
//  * @author VectorX
//  * @version V1.0
//  * @date 2024-03-11 09:00:12
//  */
// @RabbitListener(queues = {"direct-java-queue"})
// @Component
// public class MyRabbitListener2
// {
//     @RabbitHandler
//     public void receiveMessage(Message message, OrderEntity orderEntity, Channel channel) {
//         System.out.println("消息 " + orderEntity.getReceiverName() + " 被消费");
//     }
//
//     @RabbitHandler
//     public void receiveMessage2(Message message, OrderEntity orderEntity, Channel channel) {
//         // 签收消息
//         final long deliveryTag = message
//                 .getMessageProperties()
//                 .getDeliveryTag();
//         try {
//             if (deliveryTag % 2 == 0) {
//                 channel.basicAck(deliveryTag, false);
//             }
//             else {
//                 channel.basicNack(deliveryTag, false, false);
//                 // channel.basicReject(deliveryTag, false);
//             }
//         }
//         catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
