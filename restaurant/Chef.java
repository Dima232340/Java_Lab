import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Chef implements Runnable {
    private final int id;
    private final OrderQueue orderQueue;
    private final Random random;
    private volatile boolean isWorking;
    private final AtomicInteger ordersCooked;

    public Chef(int id, OrderQueue orderQueue) {
        this.id = id;
        this.orderQueue = orderQueue;
        this.random = new Random();
        this.isWorking = true;
        this.ordersCooked = new AtomicInteger(0);
    }

    @Override
    public void run() {
        System.out.printf("[ПОВАР%d] Начал работу%n", id);

        try {
            while (isWorking && !Thread.currentThread().isInterrupted()) {
                // Берем заказ из очереди
                Order order = orderQueue.takeOrderFromKitchen();

                System.out.printf("[ПОВАР%d] Готовит заказ %s (%s)%n",
                        id, order.getId().substring(0, 8), order.getDishName());

                // Имитация времени приготовления
                int cookingTime = getCookingTime(order.getDishName());
                Thread.sleep(cookingTime);

                // Помечаем заказ как готовый
                orderQueue.markOrderAsReady(order);
                ordersCooked.incrementAndGet();

                System.out.printf("[ПОВАР%d] Приготовил заказ %s за %d мс | Всего приготовлено: %d%n",
                        id, order.getId().substring(0, 8), cookingTime, ordersCooked.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[ПОВАР%d] Прерван%n", id);
        } finally {
            System.out.printf("[ПОВАР%d] Завершил работу. Приготовил %d заказов%n",
                    id, ordersCooked.get());
        }
    }

    private int getCookingTime(String dishName) {
        return switch (dishName) {
            case "Салат" -> random.nextInt(1000) + 500;
            case "Суп" -> random.nextInt(1500) + 1000;
            case "Паста" -> random.nextInt(2000) + 1500;
            case "Пицца" -> random.nextInt(2500) + 2000;
            case "Стейк" -> random.nextInt(3000) + 2500;
            case "Десерт" -> random.nextInt(1500) + 1000;
            default -> random.nextInt(2000) + 1000;
        };
    }

    public void stop() {
        isWorking = false;
    }

    public int getOrdersCooked() {
        return ordersCooked.get();
    }
}