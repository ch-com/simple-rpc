package org.example;

import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ch
 * @date 2023/4/22
 */
public class EchoImpl implements ExampleService {
    private AtomicInteger count = new AtomicInteger(0);
    @Override
    public String hello(String param) throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        int f = count.getAndIncrement();
        Thread.sleep(100);
        return param + "1";
    }
}
