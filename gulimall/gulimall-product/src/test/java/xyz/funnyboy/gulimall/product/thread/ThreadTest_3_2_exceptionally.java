package xyz.funnyboy.gulimall.product.thread;

import java.util.concurrent.*;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_3_2_exceptionally
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
                }, executor)
                // exceptionally，可以感知异常，并返回自定义默认值
                .exceptionally(throwable -> {
                    return 0;
                });

        final Integer integer = supplyAsync.get();
        System.out.println("返回数据：" + integer);

        // 当前线程：pool-1-thread-1
        // 运行结果...5
        // 异步任务完成...感知到返回值为：5异常：null
        // 返回数据：5

        // ===================================================================================================================

        // 当前线程：pool-1-thread-1
        // 异步任务完成...感知到返回值为：null异常：java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
        // 返回数据：0
    }

}
