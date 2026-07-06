package models;

import interfaces.Manageable;

/**
 * Базовый абстрактный класс для всех облачных ресурсов.
 * Реализует общий контракт Manageable и хранит общие поля,
 * доступные наследникам (protected).
 */
public abstract class CloudResource implements Manageable {

    protected String id;
    protected String name;
    protected boolean status;          // true - запущен, false - остановлен
    protected double loadPercentage;   // 0.0 .. 100.0

    public CloudResource(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = false;
        this.loadPercentage = 0.0;
    }

    /**
     * Каждый конкретный ресурс обязан вернуть подробное описание себя.
     */
    public abstract String getDetails();

    // ---- Manageable ----

    @Override
    public void start() {
        this.status = true;
    }

    @Override
    public void stop() {
        this.status = false;
    }

    @Override
    public boolean isActive() {
        return this.status;
    }

    // ---- Геттеры и сеттеры ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getLoadPercentage() {
        return loadPercentage;
    }

    public void setLoadPercentage(double loadPercentage) {
        this.loadPercentage = loadPercentage;
    }
}
