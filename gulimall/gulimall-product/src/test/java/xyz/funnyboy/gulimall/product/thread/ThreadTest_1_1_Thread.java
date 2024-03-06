package xyz.funnyboy.gulimall.product.thread;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-03-01 16:30:53
 */
public class ThreadTest_1_1_Thread
{
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main......start...");
        final Thread01 thread01 = new Thread01();
        thread01.start();
        System.out.println("main......end...");

        // main......start...
        // main......end...
        // 当前线程：Thread-0
        // 运行结果：5
    }

    public static class Thread01 extends Thread
    {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread
                    .currentThread()
                    .getName());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }
}
