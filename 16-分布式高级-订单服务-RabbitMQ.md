# RabbitMQ

## 1、安装 RabbitMQ

```bash
# 安装 RabbitMQ 镜像
docker run -d --name rabbitmq \
-p 5671:5671 -p 5672:5672 \
-p 4369:4369 -p 25672:25672 \
-p 15671:15671 -p 15672:15672 \
rabbitmq:management

# 开机自启
docker update rabbitmq --restart=always

# 查看容器
docker images
```

端口含义

| 端口          | 含义                   |
| ------------- | ---------------------- |
| `4369, 25672` | Erlang 发现 & 集群端口 |
| `5672, 5671`  | AMQP 端口              |
| `15672`       | web 管理后台端口       |



## 2、依赖

```xml
<!-- AMQP(Advanced Message Queue Protocol, 高级消息队列协议) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```



## 3、启动类

```java
// 启用 RabbitMQ
@EnableRabbit
```



## 4、配置文件

```yaml
spring:
  # RabbitMQ 配置
  rabbitmq:
    host: 192.168.56.10
    port: 5672
    virtual-host: /
    publisher-confirm-type: correlated  # 开启发送端确认
    publisher-returns: true  # 开启发送端消息抵达队列的确认
    template:
      mandatory: true  # 开启后只要消息到达队列，将以异步方式优先回调return-confirm [开不开都行]
    listener:
      direct:
        acknowledge-mode: manual  # 开启消费端手动确认消息
```



## 5、配置类

`MyRabbitConfig`

```java
@Configuration
public class MyRabbitConfig
{
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

`MyRabbitMQConfig`

```java
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
```



## 6、监听类

`OrderCloseListener`

```java
@Slf4j
@Service
@RabbitListener(queues = {MyRabbitMQConfig.RELEASE_QUEUE})
public class OrderCloseListener
{
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        log.info("检测是否需要关闭订单......");
        final long deliveryTag = message
                .getMessageProperties()
                .getDeliveryTag();
        try {
            orderService.closeOrder(entity);
            channel.basicAck(deliveryTag, false);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            // 若执行出现异常，重新放回队列中
            channel.basicReject(deliveryTag, true);
        }
    }
}
```



## 7、发送消息

`OrderServiceImpl`

```java
@Autowired
private RabbitTemplate rabbitTemplate;

@Override
public void closeOrder(OrderEntity entity) {
    final OrderEntity byId = this.getById(entity.getId());
    /**
         * 超时未付款，订单改状态
         */
    if ((int) byId.getStatus() == OrderConstant.OrderStatusEnum.CREATE_NEW.getCode()) {
        log.info("超时未付款，订单改状态......");
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(entity.getId());
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CANCLED.getCode());
        this.updateById(orderEntity);

        /**
             * 告诉库存服务订单关闭，库存解锁
             */
        log.info("告诉库存服务订单关闭，库存解锁......");
        OrderTO orderTO = new OrderTO();
        BeanUtils.copyProperties(orderEntity, orderTO);
        rabbitTemplate.convertAndSend(MyRabbitMQConfig.EXCHANGE, MyRabbitMQConfig.RELEASE_OTHER_ROUTING_KEY, orderTO);
    }
    else {
        log.info("非待付款状态，无需关闭订单......");
    }
}
```

