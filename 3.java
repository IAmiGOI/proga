package logic;

import models.CloudResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Главный класс управления облачной инфраструктурой.
 * Хранит проекты с ресурсами и список политик автоматизации,
 * не завязываясь на конкретные типы ресурсов.
 */
public class CloudManager {

    private final Map<String, List<CloudResource>> projects = new ConcurrentHashMap<>();
    private final List<ResourcePolicy<? extends CloudResource>> policies = new ArrayList<>();

    public void addResource(String projectName, CloudResource resource) {
        projects.computeIfAbsent(projectName, k -> new ArrayList<>()).add(resource);
    }

    public void addPolicy(ResourcePolicy<?> policy) {
        policies.add(policy);
    }

    public Map<String, List<CloudResource>> getProjects() {
        return projects;
    }

    public List<ResourcePolicy<? extends CloudResource>> getPolicies() {
        return policies;
    }

    /**
     * Ищет ресурс по ID во всех проектах.
     */
    public CloudResource getResourceById(String id) {
        for (List<CloudResource> resources : projects.values()) {
            for (CloudResource resource : resources) {
                if (resource.getId().equals(id)) {
                    return resource;
                }
            }
        }
        return null;
    }

    /**
     * Применяет каждую политику к каждому ресурсу во всех проектах.
     *
     * Совместимость типов: ResourcePolicy<T> хранит Predicate<T>/Consumer<T>.
     * Из-за стирания типов (type erasure) в рантайме мы не знаем T напрямую,
     * поэтому "сырое" приведение policy к ResourcePolicy<CloudResource> всегда
     * компилируется и вызывается без ошибки на уровне JVM. Но когда лямбда,
     * переданная как Predicate<VirtualMachine>/Consumer<VirtualMachine>, реально
     * вызывает специфичные для VM методы, компилятор генерирует синтетический
     * bridge-метод, который приводит аргумент к VirtualMachine - и если
     * фактический ресурс другого типа, здесь и произойдет ClassCastException.
     * Ловим это исключение, чтобы политика просто "не сработала" для чужого
     * типа ресурса, вместо падения всей системы.
     */
    @SuppressWarnings("unchecked")
    public void applyAllPolicies() {
        for (List<CloudResource> resources : projects.values()) {
            for (CloudResource resource : resources) {
                for (ResourcePolicy<?> policy : policies) {
                    try {
                        ResourcePolicy<CloudResource> rawPolicy = (ResourcePolicy<CloudResource>) policy;
                        rawPolicy.apply(resource);
                    } catch (ClassCastException e) {
                        // Ресурс не совместим по типу с данной политикой - пропускаем.
                    }
                }
            }
        }
    }

    /**
     * Возвращает единый плоский поток всех ресурсов из всех проектов.
     */
    public Stream<CloudResource> getAnalyticsStream() {
        return projects.values().stream().flatMap(List::stream);
    }
}
