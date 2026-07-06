package models;

/**
 * Виртуальная машина - конкретный облачный ресурс.
 */
public class VirtualMachine extends CloudResource {

    private int ramSize; // объем оперативной памяти, ГБ

    public VirtualMachine(String id, String name, int ramSize) {
        super(id, name);
        this.ramSize = ramSize;
    }

    public int getRamSize() {
        return ramSize;
    }

    public void setRamSize(int ramSize) {
        this.ramSize = ramSize;
    }

    @Override
    public String getDetails() {
        return String.format(
                "[VirtualMachine] id=%s, name=%s, status=%s, load=%.1f%%, ramSize=%dGB",
                id, name, status ? "running" : "stopped", loadPercentage, ramSize
        );
    }
}
