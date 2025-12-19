import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RestaurantMonitor implements Runnable {
    private final OrderQueue orderQueue;
    private volatile boolean monitoring;

    public RestaurantMonitor(OrderQueue orderQueue) {
        this.orderQueue = orderQueue;
        this.monitoring = true;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            if (monitoring) {
                System.out.println("\n=== МОНИТОРИНГ СИСТЕМЫ ===");
                System.out.printf("Очередь на кухне: %d заказов%n", orderQueue.getKitchenQueueSize());

                if (orderQueue.getKitchenQueueSize() > 5) {
                    System.out.println("ВНИМАНИЕ: Высокая загрузка кухни!");
                }
                System.out.println("=========================\n");
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        monitoring = false;
    }
}