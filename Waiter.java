import java.util.Random;

public class Waiter implements Runnable {
    private final int id;
    private final OrderQueue orderQueue;
    private final Random random;
    private volatile boolean isWorking;
    private int ordersServed;

    public Waiter(int id, OrderQueue orderQueue) {
        this.id = id;
        this.orderQueue = orderQueue;
        this.random = new Random();
        this.isWorking = true;
        this.ordersServed = 0;
    }

    @Override
    public void run() {
        System.out.printf("[ОФИЦИАНТ%d] Начал работу%n", id);

        try {
            while (isWorking && !Thread.currentThread().isInterrupted()) {
                // Имитация приема заказа от клиента
                Thread.sleep(random.nextInt(2000) + 1000);

                if (!isWorking)
                    break;

                // Создание нового заказа
                int clientId = random.nextInt(1000);
                String[] dishes = { "Пицца", "Паста", "Стейк", "Салат", "Суп", "Десерт" };
                String dish = dishes[random.nextInt(dishes.length)];

                Order order = new Order(dish, clientId);
                System.out.printf("[ОФИЦИАНТ%d] Принял заказ %s от клиента%d%n",
                        id, order.getId().substring(0, 8), clientId);

                // Передача заказа на кухню
                orderQueue.addOrderToKitchen(order);

                // Ожидание готовности заказа
                Order readyOrder = orderQueue.waitForOrderReady(order.getId(), id);

                if (readyOrder != null && readyOrder.isReady()) {
                    // Доставка заказа клиенту
                    Thread.sleep(random.nextInt(1000) + 500);
                    readyOrder.setDelivered(true);
                    ordersServed++;

                    System.out.printf("[ОФИЦИАНТ%d] Доставил заказ %s клиенту%d | Всего доставлено: %d%n",
                            id, readyOrder.getId().substring(0, 8), clientId, ordersServed);

                    // Удаляем заказ из системы
                    orderQueue.takeReadyOrder(readyOrder.getId());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[ОФИЦИАНТ%d] Прерван%n", id);
        } finally {
            System.out.printf("[ОФИЦИАНТ%d] Завершил работу. Обслужил %d заказов%n", id, ordersServed);
        }
    }

    public void stop() {
        isWorking = false;
    }

    public int getOrdersServed() {
        return ordersServed;
    }
}