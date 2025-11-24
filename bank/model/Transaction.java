package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс для представления банковской транзакции
 */
public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String description;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Геттеры
    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f руб. - %s",
                timestamp.format(FORMATTER), type, amount, description);
    }
}