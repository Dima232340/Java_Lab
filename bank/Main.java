package bank;

import bank.service.BankService;

import java.util.Scanner;

/**
 * Главный класс банковского приложения
 * Реализует консольное меню для управления счетами
 */
public class Main {
    private static final BankService bankService = new BankService();
    private static final Scanner scanner = new Scanner(System.in);

    // Константы меню
    private static final int OPEN_ACCOUNT = 1;
    private static final int DEPOSIT = 2;
    private static final int WITHDRAW = 3;
    private static final int SHOW_BALANCE = 4;
    private static final int SHOW_TRANSACTIONS = 5;
    private static final int SEARCH_TRANSACTIONS = 6;
    private static final int SHOW_ALL_ACCOUNTS = 7;
    private static final int EXIT = 0;

    public static void main(String[] args) {
        System.out.println("=== БАНКОВСКАЯ СИСТЕМА ===");
        showMenu();

        boolean running = true;
        while (running) {
            try {
                System.out.print("\nВыберите действие: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case OPEN_ACCOUNT:
                        bankService.openAccount();
                        break;
                    case DEPOSIT:
                        bankService.deposit();
                        break;
                    case WITHDRAW:
                        bankService.withdraw();
                        break;
                    case SHOW_BALANCE:
                        bankService.showBalance();
                        break;
                    case SHOW_TRANSACTIONS:
                        bankService.showTransactions();
                        break;
                    case SEARCH_TRANSACTIONS:
                        bankService.searchTransactions();
                        break;
                    case SHOW_ALL_ACCOUNTS:
                        bankService.showAllAccounts();
                        break;
                    case EXIT:
                        running = false;
                        System.out.println("Выход из системы. До свидания!");
                        break;
                    default:
                        System.out.println("Неверный выбор! Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число от 0 до 7!");
            } catch (Exception e) {
                System.out.println("Произошла непредвиденная ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Отображение главного меню
     */
    private static void showMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println(OPEN_ACCOUNT + ". Открыть новый счет");
        System.out.println(DEPOSIT + ". Пополнить счет");
        System.out.println(WITHDRAW + ". Снять деньги");
        System.out.println(SHOW_BALANCE + ". Показать баланс");
        System.out.println(SHOW_TRANSACTIONS + ". История транзакций");
        System.out.println(SEARCH_TRANSACTIONS + ". Поиск транзакций");
        System.out.println(SHOW_ALL_ACCOUNTS + ". Показать все счета");
        System.out.println(EXIT + ". Выход");
    }
}