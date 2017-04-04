package com.jay;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  多线程
 */

class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                q.put(String.valueOf(i));  //每隔一秒，放入阻塞队列
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while(true){ //从阻塞队列一直取值
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



public class MultiThread {

    public static void testBlockingQueue(){
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10); //指定初始容量
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue), "Consumer1").start();
        new Thread(new Consumer(queue), "Consumer2").start();
    }
    //休眠
    public static void sleep(int mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0); //原子的方式更新值
    public static void testWithAtomic(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j = 0; j < 100; j++) {
                        System.out.println(atomicInteger.incrementAndGet());  //增加并返回值
                    }
                }
            }).start();
        }
    }
    public static void testWithoutAtomic(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j = 0; j < 100; j++) {
                        count++;
                        System.out.println(count);
                    }
                }
            }).start();
        }
    }

    private static ThreadLocal<Integer> threadLocalUserId = new ThreadLocal<Integer>(); //线程本地变量
    private static int userId;

    public static void testThreadLocal(){
        for (int i = 0; i < 10; i++) {
            final int finalI = i;  //这里必须声明为final 生命周期问题
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserId.set(finalI);//多个线程之间互不干扰
                    sleep(1000);
                    System.out.println("ThreadLocal: " + threadLocalUserId.get());
                }
            }).start();
        }

        for (int i = 0; i < 10; i++) {
            final int finalI = i;  //这里必须声明为final 生命周期问题
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userId = finalI;
                    sleep(1000);
                    System.out.println("NonThreadLocal: " + userId);
                }
            }).start();
        }
    }

    public static void testExecutor(){
        //ExecutorService service = Executors.newSingleThreadExecutor(); //Executors工具类创建线程执行服务，单线程
        ExecutorService service = Executors.newFixedThreadPool(2); //创建有2个线程可用的线程池
        service.submit(new Runnable() {  //单独的线程执行任务
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Execute1 " + i);
                }
            }
        });

        service.submit(new Runnable() {  //因为只有一个另开的线程，该任务等待执行
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    sleep(1000);
                    System.out.println("Execute2 " + i);
                }
            }
        });

        service.shutdown();  //任务执行后关闭服务
        while(!service.isTerminated()){ //判断任务是否完成
            sleep(1000);
            System.out.println("waiting for termination!!");
        }

    }


    public static void testFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future  = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);
                return 1;
//                throw new IllegalArgumentException("一个异常");
            }
        });

        service.shutdown(); //关闭服务

        try {
//            System.out.println(future.get());
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));  //等待100ms,TimeoutException
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //testBlockingQueue();
        //testWithAtomic();
        //testWithoutAtomic();
        //testThreadLocal();
        //testExecutor();
        testFuture();
    }

}