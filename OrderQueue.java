import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class OrderQueue {
    private final BlockingQueue<Order> kitchenQueue;
    private final ConcurrentHashMap<String, Order> readyOrders;
    private final ConcurrentHashMap<String, Object> orderLocks;
    private volatile boolean isRunning;

    public OrderQueue(int capacity) {
        this.kitchenQueue = new LinkedBlockingQueue<>(capacity);
        this.readyOrders = new ConcurrentHashMap<>();
        this.orderLocks = new ConcurrentHashMap<>();
        this.isRunning = true;
    }

    public void addOrderToKitchen(Order order) throws InterruptedException {
        if (!isRunning)
            return;

        Object lock = new Object();
        orderLocks.put(order.getId(), lock);
        order.setCooking();
        kitchenQueue.put(order);

        System.out.printf("[ОЧЕРЕДЬ] Заказ %s добавлен в очередь кухни. Очередь: %d%n",
                order.getId().substring(0, 8), kitchenQueue.size());
    }

    public Order takeOrderFromKitchen() throws InterruptedException {
        Order order = kitchenQueue.take();
        return order;
    }

    public void markOrderAsReady(Order order) {
        order.setReady(true);
        readyOrders.put(order.getId(), order);

        Object lock = orderLocks.get(order.getId());
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        System.out.printf("[КУХНЯ] Заказ %s готов к выдаче%n", order.getId().substring(0, 8));
    }

    public Order waitForOrderReady(String orderId, long waiterId) throws InterruptedException {
        Order order = readyOrders.get(orderId);
        if (order != null && order.isReady()) {
            return order;
        }

        Object lock = orderLocks.get(orderId);
        if (lock != null) {
            synchronized (lock) {
                while (isRunning && !Thread.currentThread().isInterrupted()) {
                    order = readyOrders.get(orderId);
                    if (order != null && order.isReady()) {
                        break;
                    }
                    System.out.printf("[ОФИЦИАНТ%d] Ожидает готовности заказа %s%n",
                            waiterId, orderId.substring(0, 8));
                    lock.wait(5000); // Ожидание с таймаутом (в миллисекундах)
                }
            }
        }

        return readyOrders.get(orderId);
    }

    public Order takeReadyOrder(String orderId) {
        Order order = readyOrders.remove(orderId);
        orderLocks.remove(orderId);
        return order;
    }

    public int getKitchenQueueSize() {
        return kitchenQueue.size();
    }

    public void shutdown() {
        isRunning = false;
        kitchenQueue.clear();

        // Уведомляем все ожидающие потоки
        orderLocks.values().forEach(lock -> {
            synchronized (lock) {
                lock.notifyAll();
            }
        });
    }
}