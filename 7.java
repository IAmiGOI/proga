package models;

/**
 * Балансировщик нагрузки - конкретный облачный ресурс.
 */
public class LoadBalancer extends CloudResource {

    private int activeConnections; // количество активных сетевых соединений

    public LoadBalancer(String id, String name, int activeConnections) {
        super(id, name);
        this.activeConnections = activeConnections;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }

    @Override
    public String getDetails() {
        return String.format(
                "[LoadBalancer] id=%s, name=%s, status=%s, load=%.1f%%, activeConnections=%d",
                id, name, status ? "running" : "stopped", loadPercentage, activeConnections
        );
    }
}
