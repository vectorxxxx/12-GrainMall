package xyz.funnyboy.gulimall.product.thread;

import java.util.concurrent.*;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_4_2_thenAcceptAsync
{
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            // 核心线程数
            5,
            // 最大线程数
            200,
            // 存活时间
            10,
            // 时间单位
            TimeUnit.SECONDS,
            // 无界队列
            new LinkedBlockingDeque<>(100000),
            // 线程工厂
            Executors.defaultThreadFactory(),
            // 拒绝策略
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        CompletableFuture
                // supplyAsync，带线程返回值
                .supplyAsync(() -> {
                    System.out.println("当前线程：" + Thread
                            .currentThread()
                            .getName());
                    int i = 10 / 2;
                    System.out.println("运行结果..." + i);
                    return i;
                }, executor)
                // thenAcceptAsync，继续执行，接受上一个任务的返回结果,自己执行完没有返回结果
                .thenAcceptAsync(res -> {
                    System.out.println("任务二启动了...拿到了上一步的结果：" + res);
                }, executor);

        // 当前线程：pool-1-thread-1
        // 运行结果...5
        // 任务二启动了...拿到了上一步的结果：5
    }

}
