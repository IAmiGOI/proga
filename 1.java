package main;

import logic.CloudManager;
import logic.ResourcePolicy;
import models.CloudDatabase;
import models.CloudResource;
import models.LoadBalancer;
import models.VirtualMachine;

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Консольное приложение для управления облачной инфраструктурой.
 *
 * Индивидуальный вариант №1:
 *  - Политика: вертикальное масштабирование VirtualMachine
 *    (если активна и loadPercentage > 90.0, увеличить ramSize на 8 ГБ).
 *  - Аналитика: подсчитать количество ресурсов каждого класса
 *    во всем облаке (Map<String, Long>).
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CloudManager manager = new CloudManager();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addResource();
                    break;
                case "2":
                    toggleResource();
                    break;
                case "3":
                    addPhantomLoadResetPolicy();
                    break;
                case "4":
                    addVariantPolicy();
                    break;
                case "5":
                    manager.applyAllPolicies();
                    System.out.println("Все политики применены.");
                    break;
                case "6":
                    runAnalytics();
                    break;
                case "7":
                    running = false;
                    System.out.println("Выход из программы.");
                    break;
                default:
                    System.out.println("Некорректный пункт меню, попробуйте снова.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Управление облачной инфраструктурой =====");
        System.out.println("1. Добавить новый ресурс в проект");
        System.out.println("2. Запустить/остановить ресурс");
        System.out.println("3. Добавить базовую политику 'Сброс фантомной нагрузки'");
        System.out.println("4. Добавить политику автоматизации (индивидуальный вариант)");
        System.out.println("5. Применить все политики (applyAllPolicies)");
        System.out.println("6. Выполнить аналитический запрос");
        System.out.println("7. Выход");
        System.out.print("Выберите пункт: ");
    }

    // ---- 1. Добавление ресурса ----

    private static void addResource() {
        System.out.print("Тип ресурса (1-VM, 2-DB, 3-LB): ");
        String type = scanner.nextLine().trim();

        System.out.print("ID ресурса: ");
        String id = scanner.nextLine().trim();

        System.out.print("Имя ресурса: ");
        String name = scanner.nextLine().trim();

        System.out.print("Название проекта: ");
        String projectName = scanner.nextLine().trim();

        CloudResource resource;

        switch (type) {
            case "1": {
                int ramSize = readInt("Объем RAM (ГБ): ");
                resource = new VirtualMachine(id, name, ramSize);
                break;
            }
            case "2": {
                double storageUsed = readDouble("Занятое место (ТБ): ");
                resource = new CloudDatabase(id, name, storageUsed);
                break;
            }
            case "3": {
                int connections = readInt("Активные соединения: ");
                resource = new LoadBalancer(id, name, connections);
                break;
            }
            default:
                System.out.println("Неизвестный тип ресурса, отмена.");
                return;
        }

        manager.addResource(projectName, resource);
        System.out.println("Ресурс добавлен: " + resource.getDetails());
    }

    // ---- 2. Старт/стоп ресурса ----

    private static void toggleResource() {
        System.out.print("ID ресурса: ");
        String id = scanner.nextLine().trim();

        CloudResource resource = manager.getResourceById(id);
        if (resource == null) {
            System.out.println("Ресурс с таким ID не найден.");
            return;
        }

        System.out.println("Текущий статус: " + (resource.isActive() ? "запущен" : "остановлен"));
        System.out.print("Что сделать? (1-Запустить, 2-Остановить): ");
        String action = scanner.nextLine().trim();

        if ("1".equals(action)) {
            resource.start();
            System.out.println("Ресурс запущен.");
        } else if ("2".equals(action)) {
            resource.stop();
            System.out.println("Ресурс остановлен.");
        } else {
            System.out.println("Некорректное действие.");
        }
    }

    // ---- 3. Базовая политика "Сброс фантомной нагрузки" ----

    private static void addPhantomLoadResetPolicy() {
        ResourcePolicy<CloudResource> policy = new ResourcePolicy<>(
                "Сброс фантомной нагрузки",
                resource -> !resource.isActive() && resource.getLoadPercentage() > 0.0,
                resource -> resource.setLoadPercentage(0.0)
        );
        manager.addPolicy(policy);
        System.out.println("Политика 'Сброс фантомной нагрузки' добавлена.");
    }

    // ---- 4. Индивидуальная политика (Вариант 1: вертикальное масштабирование VM) ----

    private static void addVariantPolicy() {
        ResourcePolicy<VirtualMachine> policy = new ResourcePolicy<>(
                "Вертикальное масштабирование VM",
                vm -> vm.isActive() && vm.getLoadPercentage() > 90.0,
                vm -> vm.setRamSize(vm.getRamSize() + 8)
        );
        manager.addPolicy(policy);
        System.out.println("Политика 'Вертикальное масштабирование VM' добавлена.");
    }

    // ---- 6. Аналитика (Вариант 1: количество ресурсов каждого класса) ----

    private static void runAnalytics() {
        Map<String, Long> countByClass = manager.getAnalyticsStream()
                .collect(Collectors.groupingBy(
                        resource -> resource.getClass().getSimpleName(),
                        Collectors.counting()
                ));

        if (countByClass.isEmpty()) {
            System.out.println("В облаке пока нет ресурсов.");
            return;
        }

        System.out.println("Количество ресурсов по классам:");
        countByClass.forEach((className, count) ->
                System.out.println("  " + className + ": " + count));
    }

    // ---- Вспомогательные методы ввода ----

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }
}
