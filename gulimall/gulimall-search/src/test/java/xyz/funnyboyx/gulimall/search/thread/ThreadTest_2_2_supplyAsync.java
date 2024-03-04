package xyz.funnyboyx.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_2_2_supplyAsync
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
        // supplyAsync，带线程返回值
        final CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread
                    .currentThread()
                    .getName());
            int i = 10 / 2;
            System.out.println("运行结果..." + i);
            return i;
        }, executor);

        final Integer integer = supplyAsync.get();
        System.out.println("返回数据：" + integer);

        // 当前线程：pool-1-thread-1
        // 运行结果...5
        // 返回数据：5
    }

}
