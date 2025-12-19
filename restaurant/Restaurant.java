import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final OrderQueue orderQueue;
    private final List<Thread> waiterThreads;
    private final List<Waiter> waiters;
    private final ExecutorService kitchenExecutor;
    private final List<Chef> chefs;
    private final RestaurantMonitor monitor;
    private final int maxQueueSize = 10;

    public Restaurant(int numWaiters, int numChefs) {
        this.orderQueue = new OrderQueue(maxQueueSize);
        this.waiterThreads = new ArrayList<>();
        this.waiters = new ArrayList<>();
        this.kitchenExecutor = Executors.newFixedThreadPool(numChefs);
        this.chefs = new ArrayList<>();

        // Создание официантов
        for (int i = 1; i <= numWaiters; i++) {
            Waiter waiter = new Waiter(i, orderQueue);
            waiters.add(waiter);
            Thread waiterThread = new Thread(waiter, "Waiter-" + i);
            waiterThreads.add(waiterThread);
        }

        // Создание поваров
        for (int i = 1; i <= numChefs; i++) {
            Chef chef = new Chef(i, orderQueue);
            chefs.add(chef);
            kitchenExecutor.submit(chef);
        }

        // Создание монитора
        this.monitor = new RestaurantMonitor(orderQueue);
        Thread monitorThread = new Thread(monitor, "Monitor");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void start() {
        System.out.println("================ РЕСТОРАН ОТКРЫТ ================");
        System.out.println("Официантов: " + waiterThreads.size());
        System.out.println("Поваров: " + chefs.size());
        System.out.println("Макс. размер очереди: " + maxQueueSize);
        System.out.println("================================================");

        // Запуск официантов
        waiterThreads.forEach(Thread::start);
    }

    public void shutdown() {
        System.out.println("\n================ ЗАКРЫТИЕ РЕСТОРАНА ================");

        // Останавливаем мониторинг
        monitor.stopMonitoring();

        // Останавливаем официантов
        waiters.forEach(Waiter::stop);

        // Прерываем потоки официантов
        waiterThreads.forEach(Thread::interrupt);

        // Ожидаем завершения официантов
        for (Thread thread : waiterThreads) {
            try {
                thread.join(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Останавливаем кухню
        orderQueue.shutdown();
        kitchenExecutor.shutdown();

        // Останавливаем поваров
        chefs.forEach(Chef::stop);

        try {
            if (!kitchenExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                kitchenExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            kitchenExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        printStatistics();
        System.out.println("===================================================");
    }

    private void printStatistics() {
        System.out.println("\n====== СТАТИСТИКА РАБОТЫ РЕСТОРАНА ======");

        int totalOrdersServed = 0;
        for (Waiter waiter : waiters) {
            totalOrdersServed += waiter.getOrdersServed();
        }

        int totalOrdersCooked = 0;
        for (Chef chef : chefs) {
            totalOrdersCooked += chef.getOrdersCooked();
        }

        System.out.println("Всего заказов принято: " + totalOrdersServed);
        System.out.println("Всего заказов приготовлено: " + totalOrdersCooked);

        System.out.println("\nОфицианты:");
        for (int i = 0; i < waiters.size(); i++) {
            System.out.printf("  Официант%d: %d заказов%n",
                    i + 1, waiters.get(i).getOrdersServed());
        }

        System.out.println("\nПовара:");
        for (int i = 0; i < chefs.size(); i++) {
            System.out.printf("  Повар%d: %d заказов%n",
                    i + 1, chefs.get(i).getOrdersCooked());
        }
    }

    public static void main(String[] args) {
        // Создаем ресторан с 3 официантами и 2 поварами
        Restaurant restaurant = new Restaurant(3, 2);

        // Запускаем ресторан
        restaurant.start();

        // Работаем 30 секунд
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Закрываем ресторан
        restaurant.shutdown();

        // Даем время на вывод статистики
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}