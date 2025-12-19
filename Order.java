import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private final String id;
    private final String dishName;
    private final int clientId;
    private final long orderTime;
    private volatile boolean isReady;
    private volatile boolean isDelivered;
    private final AtomicInteger status; // 0-создан, 1-готовится, 2-готов, 3-выдан

    public Order(String dishName, int clientId) {
        this.id = UUID.randomUUID().toString();
        this.dishName = dishName;
        this.clientId = clientId;
        this.orderTime = System.currentTimeMillis();
        this.isReady = false;
        this.isDelivered = false;
        this.status = new AtomicInteger(0);
    }

    public String getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public int getClientId() {
        return clientId;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
        if (ready)
            status.set(2);
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
        if (delivered)
            status.set(3);
    }

    public void setCooking() {
        status.set(1);
    }

    public int getStatus() {
        return status.get();
    }

    @Override
    public String toString() {
        return String.format("Заказ#%s [%s] для клиента%d | статус: %s",
                id.substring(0, 8), dishName, clientId,
                getStatusString());
    }

    private String getStatusString() {
        switch (status.get()) {
            case 0:
                return "создан";
            case 1:
                return "готовится";
            case 2:
                return "готов";
            case 3:
                return "доставлен";
            default:
                return "неизвестно";
        }
    }
}