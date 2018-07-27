package com.github.filipmalczak.vent.web.orchestration;

import com.github.filipmalczak.vent.VentWebServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestServerManager {
    protected static int port;
    protected static ConfigurableApplicationContext context;

    protected static AtomicInteger inUse = new AtomicInteger(0);

    protected final static long GRACE_PERIOD_MS = Long.valueOf(System.getProperty("test.server.grace", "5000"));
    protected final static ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    static {
        log.info("Server grace period: "+GRACE_PERIOD_MS+"ms");
    }

    public static synchronized void start(){
        log.info("Requesting server start");
        int demand = inUse.incrementAndGet();
        log.info("Current server demand: "+demand);
        startContextIfNeeded();
    }

    public static synchronized void stop(){
        log.info("Server demand satisfied");
        int demand = inUse.decrementAndGet();
        log.info("Current server demand: "+demand);
        scheduleServerCleanup();
    }

    private static void startContextIfNeeded(){
        if (context == null || !context.isRunning()) {
            log.info("No server available, starting an instance");
            context = SpringApplication.run(new Class[]{TestServerConfig.class, VentWebServer.class}, new String[0]);
            port = Integer.valueOf((context).getEnvironment().getProperty("local.server.port"));
            context.start();
            log.info("Server running");
        } else {
            log.info("Server instance available");
        }
    }
    private static void scheduleServerCleanup(){
        EXECUTOR_SERVICE.schedule(
            () -> {
                log.info("Grace period passed, checking demand");
                int demand = inUse.get();
                log.info("Current server demand: "+demand);
                if (demand <= 0 && context.isRunning()) {
                    log.info("No demand, stopping server");
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
