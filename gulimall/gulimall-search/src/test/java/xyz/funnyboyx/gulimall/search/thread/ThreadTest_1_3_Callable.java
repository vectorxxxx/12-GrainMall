package xyz.funnyboyx.gulimall.search.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_1_3_Callable
{
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("main......start...");
        final FutureTask futureTask = new FutureTask<>(new Thread03());
        new Thread(futureTask).start();
        final Integer i = (Integer) futureTask.get();
        System.out.println("main......end..." + i);

        // main......start...
        // 当前线程：Thread-0
        // 运行结果：5
        // main......end...5
    }

    public static class Thread03 implements Callable
    {
        @Override
        public Object call() throws Exception {
            System.out.println("当前线程：" + Thread
                    .currentThread()
                    .getName());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
}
