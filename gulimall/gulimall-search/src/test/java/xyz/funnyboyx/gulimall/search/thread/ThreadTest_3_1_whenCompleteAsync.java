package xyz.funnyboyx.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_3_1_whenCompleteAsync
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
        final CompletableFuture<Integer> supplyAsync = CompletableFuture
                // supplyAsync，带线程返回值
                .supplyAsync(() -> {
                    System.out.println("当前线程：" + Thread
                            .currentThread()
                            .getName());
                    int i = 10 / 2;
                    // int i = 10 / 0;
                    System.out.println("运行结果..." + i);
                    return i;
                }, executor)
                // whenCompleteAsync，感知结果和异常但不处理
                .whenCompleteAsync((res, exception) -> {
                    System.out.println("异步任务完成...感知到返回值为：" + res + "异常：" + exception);
                }, executor);

        final Integer integer = supplyAsync.get();
        System.out.println("返回数据：" + integer);

        // 当前线程：pool-1-thread-1
        // 运行结果...5
        // 异步任务完成...感知到返回值为：5异常：null
        // 返回数据：5

        // ===================================================================================================================

        // 当前线程：pool-1-thread-1
        // 异步任务完成...感知到返回值为：null异常：java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
        // Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
        // 	at java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:357)
        // 	at java.util.concurrent.CompletableFuture.get(CompletableFuture.java:1908)
        // 	at xyz.funnyboyx.gulimall.search.thread.ThreadTest06.main(ThreadTest06.java:45)
        // Caused by: java.lang.ArithmeticException: / by zero
        // 	at xyz.funnyboyx.gulimall.search.thread.ThreadTest06.lambda$main$0(ThreadTest06.java:36)
        // 	at java.util.concurrent.CompletableFuture$AsyncSupply.run$$$capture(CompletableFuture.java:1604)
        // 	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java)
        // 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        // 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        // 	at java.lang.Thread.run(Thread.java:748)
    }

}
