package com.github.filipmalczak.vent;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestServerManager {
    protected static int port;
    protected static ConfigurableApplicationContext context;

    protected static AtomicInteger inUse = new AtomicInteger(0);

    protected final static long GRACE_PERIOD_MS = Long.valueOf(System.getProperty("test.server.grace", "5000"));
    protected final static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public static synchronized void start(){
        inUse.incrementAndGet();
        startContextIfNeeded();
    }

    public static synchronized void stop(){
        inUse.decrementAndGet();
        scheduleServerCleanup();
    }

    private static void startContextIfNeeded(){
        if (context == null || !context.isRunning()) {
            context = SpringApplication.run(new Class[]{TestServerConfig.class, VentWebServer.class}, new String[0]);
            port = Integer.valueOf((context).getEnvironment().getProperty("local.server.port"));
            context.start();
        }
    }
    private static void scheduleServerCleanup(){
        EXECUTOR_SERVICE.schedule(
            () -> {
                //fixme safeguard agaist <0
                if (inUse.get() <= 0 && context.isRunning()) {
                    context.stop();
                    context = null;
                }
            },
            GRACE_PERIOD_MS,
            TimeUnit.MILLISECONDS
        );
    }

    public static WebClient newClient(){
        return WebClient.create("http://localhost:"+port);
    }

    public static Object getBean(String beanName){
        startContextIfNeeded();
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClass){
        startContextIfNeeded();
        return context.getBean(beanClass);
    }
}
