package models;

/**
 * Облачная база данных - конкретный облачный ресурс.
 */
public class CloudDatabase extends CloudResource {

    private double storageUsed; // занятое место на диске, ТБ

    public CloudDatabase(String id, String name, double storageUsed) {
        super(id, name);
        this.storageUsed = storageUsed;
    }

    public double getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(double storageUsed) {
        this.storageUsed = storageUsed;
    }

    @Override
    public String getDetails() {
        return String.format(
                "[CloudDatabase] id=%s, name=%s, status=%s, load=%.1f%%, storageUsed=%.1fTB",
                id, name, status ? "running" : "stopped", loadPercentage, storageUsed
        );
    }
}
