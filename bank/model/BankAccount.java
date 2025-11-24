package bank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления банковского счёта
 */
public class BankAccount {
    private final String accountNumber;
    private double balance;
    private boolean isActive;
    private final List<Transaction> transactions;
    private final String ownerName;

    // Константы для типов транзакций
    public static final String DEPOSIT = "ПОПОЛНЕНИЕ";
    public static final String WITHDRAWAL = "СНЯТИЕ";
    public static final String ACCOUNT_OPENED = "ОТКРЫТИЕ СЧЕТА";

    public BankAccount(String accountNumber, String ownerName, double initialDeposit) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер счета не может быть пустым");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя владельца не может быть пустым");
        }
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Начальный депозит не может быть отрицательным");
        }

        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialDeposit;
        this.isActive = true;
        this.transactions = new ArrayList<>();

        // Добавляем транзакцию открытия счета
        this.transactions.add(new Transaction(ACCOUNT_OPENED, initialDeposit,
                "Открытие счета. Владелец: " + ownerName));
    }

    /**
     * Пополнение счета
     */
    public void deposit(double amount) {
        validateActiveAccount();
        validatePositiveAmount(amount);

        balance += amount;
        transactions.add(new Transaction(DEPOSIT, amount, "Пополнение счета"));
    }

    /**
     * Снятие денег со счета
     */
    public boolean withdraw(double amount) {
        validateActiveAccount();
        validatePositiveAmount(amount);

        if (amount > balance) {
            return false; // Недостаточно средств
        }

        balance -= amount;
        transactions.add(new Transaction(WITHDRAWAL, amount, "Снятие наличных"));
        return true;
    }

    /**
     * Получение баланса
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Получение номера счета
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Получение имени владельца
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Проверка активности счета
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Закрытие счета
     */
    public void closeAccount() {
        this.isActive = false;
    }

    /**
     * Получение списка транзакций (копии для защиты от изменений)
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Поиск транзакций по типу
     */
    public List<Transaction> findTransactionsByType(String type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equalsIgnoreCase(type)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Поиск транзакций по минимальной сумме
     */
    public List<Transaction> findTransactionsByMinAmount(double minAmount) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= minAmount) {
                result.add(transaction);
            }
        }
        return result;
    }

    // Приватные методы валидации
    private void validateActiveAccount() {
        if (!isActive) {
            throw new IllegalStateException("Счет закрыт. Операции невозможны.");
        }
    }

    private void validatePositiveAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
    }

    @Override
    public String toString() {
        return String.format("Счет: %s, Владелец: %s, Баланс: %.2f руб., Статус: %s",
                accountNumber, ownerName, balance, isActive ? "активен" : "закрыт");
    }
}