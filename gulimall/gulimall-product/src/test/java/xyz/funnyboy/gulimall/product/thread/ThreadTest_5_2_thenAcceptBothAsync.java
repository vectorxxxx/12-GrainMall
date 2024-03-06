package xyz.funnyboy.gulimall.product.thread;

import java.util.concurrent.*;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_5_2_thenAcceptBothAsync
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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final CompletableFuture<Integer> future01 = CompletableFuture
                // supplyAsync，带线程返回值
                .supplyAsync(() -> {
                    System.out.println("任务一线程开始：" + Thread
                            .currentThread()
                            .getName());
                    int i = 10 / 2;
                    System.out.println("任务一运行结束..." + i);
                    return i;
                }, executor);

        CompletableFuture<Object> future02 = CompletableFuture
                // supplyAsync，带线程返回值
                .supplyAsync(() -> {
                    System.out.println("任务二线程开始:" + Thread
                            .currentThread()
                            .getName());
                    System.out.println("任务二运行结束....");
                    return "hello";
                }, executor);

        future01
                // thenAcceptBothAsync，获取结果并处理新任务
                .thenAcceptBothAsync(future02, (res1, res2) -> {
                    System.out.println("任务一返回值:" + res1 + "，任务二返回值:" + res2);
                }, executor);

        // 任务一线程开始：pool-1-thread-1
        // 任务二线程开始:pool-1-thread-2
        // 任务一运行结束...5
        // 任务二运行结束....
        // 任务一返回值:5，任务二返回值:hello
    }

}
