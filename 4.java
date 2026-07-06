package logic;

import models.CloudResource;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Обобщенный класс политики автоматизации ресурса.
 * Инкапсулирует условие (Predicate) и действие (Consumer),
 * что позволяет полностью избежать жестких if-else конструкций
 * при обработке разных типов ресурсов.
 *
 * @param <T> конкретный тип ресурса, к которому применяется политика
 */
public class ResourcePolicy<T extends CloudResource> {

    private final String policyName;
    private final Predicate<T> condition;
    private final Consumer<T> action;

    public ResourcePolicy(String policyName, Predicate<T> condition, Consumer<T> action) {
        this.policyName = policyName;
        this.condition = condition;
        this.action = action;
    }

    public String getPolicyName() {
        return policyName;
    }

    /**
     * Проверяет условие и, если оно истинно, выполняет действие над ресурсом.
     */
    public void apply(T resource) {
        if (condition.test(resource)) {
            action.accept(resource);
        }
    }
}
