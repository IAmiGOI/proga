package interfaces;

/**
 * Общий контракт управления любым облачным ресурсом.
 */
public interface Manageable {
    void start();
    boolean isActive();
    void stop();
}
