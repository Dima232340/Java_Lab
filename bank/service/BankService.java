package bank.service;

import bank.model.BankAccount;
import bank.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Сервис для управления банковскими операциями
 */
public class BankService {
    private final List<BankAccount> accounts;
    private final Scanner scanner;

    public BankService() {
        this.accounts = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Открытие нового счета
     */
    public void openAccount() {
        System.out.println("\n=== ОТКРЫТИЕ НОВОГО СЧЕТА ===");

        System.out.print("Введите номер счета: ");
        String accountNumber = scanner.nextLine().trim();

        // Проверка уникальности номера счета
        if (findAccountByNumber(accountNumber) != null) {
            System.out.println("Ошибка: счет с таким номером уже существует!");
            return;
        }

        System.out.print("Введите имя владельца: ");
        String ownerName = scanner.nextLine().trim();

        double initialDeposit = readPositiveDouble("Введите начальный депозит: ");

        try {
            BankAccount newAccount = new BankAccount(accountNumber, ownerName, initialDeposit);
            accounts.add(newAccount);
            System.out.println("Счет успешно открыт!");
            System.out.println(newAccount);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при открытии счета: " + e.getMessage());
        }
    }

    /**
     * Пополнение счета
     */
    public void deposit() {
        BankAccount account = findAccountByUserInput();
        if (account == null)
            return;

        double amount = readPositiveDouble("Введите сумму для пополнения: ");

        try {
            account.deposit(amount);
            System.out.printf("Счет успешно пополнен на %.2f руб. Новый баланс: %.2f руб.\n",
                    amount, account.getBalance());
        } catch (Exception e) {
            System.out.println("Ошибка при пополнении: " + e.getMessage());
        }
    }

    /**
     * Снятие денег со счета
     */
    public void withdraw() {
        BankAccount account = findAccountByUserInput();
        if (account == null)
            return;

        double amount = readPositiveDouble("Введите сумму для снятия: ");

        try {
            boolean success = account.withdraw(amount);
            if (success) {
                System.out.printf("Со счета снято %.2f руб. Новый баланс: %.2f руб.\n",
                        amount, account.getBalance());
            } else {
                System.out.println("Ошибка: недостаточно средств на счете!");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при снятии: " + e.getMessage());
        }
    }

    /**
     * Показать баланс счета
     */
    public void showBalance() {
        BankAccount account = findAccountByUserInput();
        if (account == null)
            return;

        System.out.println("\n=== ИНФОРМАЦИЯ О СЧЕТЕ ===");
        System.out.println(account);
    }

    /**
     * Показать историю транзакций
     */
    public void showTransactions() {
        BankAccount account = findAccountByUserInput();
        if (account == null)
            return;

        List<Transaction> transactions = account.getTransactions();

        System.out.println("\n=== ИСТОРИЯ ТРАНЗАКЦИЙ ===");
        if (transactions.isEmpty()) {
            System.out.println("Транзакции отсутствуют.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
        }
    }

    /**
     * Поиск транзакций по атрибутам
     */
    public void searchTransactions() {
        BankAccount account = findAccountByUserInput();
        if (account == null)
            return;

        System.out.println("\n=== ПОИСК ТРАНЗАКЦИЙ ===");
        System.out.println("1. По типу операции");
        System.out.println("2. По минимальной сумме");
        System.out.print("Выберите критерий поиска: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    searchByType(account);
                    break;
                case 2:
                    searchByAmount(account);
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число!");
        }
    }

    /**
     * Поиск по типу транзакции
     */
    private void searchByType(BankAccount account) {
        System.out.println("Типы операций: " + BankAccount.DEPOSIT + ", " + BankAccount.WITHDRAWAL);
        System.out.print("Введите тип операции: ");
        String type = scanner.nextLine().trim();

        List<Transaction> result = account.findTransactionsByType(type);
        displaySearchResults(result, "по типу: " + type);
    }

    /**
     * Поиск по минимальной сумме
     */
    private void searchByAmount(BankAccount account) {
        double minAmount = readPositiveDouble("Введите минимальную сумму: ");

        List<Transaction> result = account.findTransactionsByMinAmount(minAmount);
        displaySearchResults(result, "с суммой от: " + minAmount + " руб.");
    }

    /**
     * Отображение результатов поиска
     */
    private void displaySearchResults(List<Transaction> transactions, String criteria) {
        System.out.println("\n=== РЕЗУЛЬТАТЫ ПОИСКА (" + criteria + ") ===");

        if (transactions.isEmpty()) {
            System.out.println("Транзакции не найдены.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
            System.out.println("Найдено транзакций: " + transactions.size());
        }
    }

    /**
     * Поиск счета по номеру
     */
    private BankAccount findAccountByNumber(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Ввод номера счета пользователем
     */
    private BankAccount findAccountByUserInput() {
        if (accounts.isEmpty()) {
            System.out.println("Нет открытых счетов!");
            return null;
        }

        System.out.print("Введите номер счета: ");
        String accountNumber = scanner.nextLine().trim();

        BankAccount account = findAccountByNumber(accountNumber);
        if (account == null) {
            System.out.println("Счет с номером '" + accountNumber + "' не найден!");
        }

        return account;
    }

    /**
     * Чтение положительного числа с консоли
     */
    private double readPositiveDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("Ошибка: введите положительное число!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число!");
            }
        }
    }

    /**
     * Показать все счета
     */
    public void showAllAccounts() {
        System.out.println("\n=== ВСЕ СЧЕТА ===");
        if (accounts.isEmpty()) {
            System.out.println("Нет открытых счетов.");
        } else {
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + ". " + accounts.get(i));
            }
        }
    }
}