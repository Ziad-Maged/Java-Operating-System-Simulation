import java.io.Serializable;

public class Process implements Serializable {
    private final String processName;
    private final PCB processControlBlock;
    private final int totalExecutionTime;
    private int currentExecutionTime;
    private boolean inDisk;
    private final int requiredSizeInMemory;
    private int currentTimeSlice;

    public Process(String processName, PCB processControlBlock, int totalExecutionTime, int requiredSizeInMemory) {
        this.processName = processName;
        this.processControlBlock = processControlBlock;
        this.totalExecutionTime = totalExecutionTime;
        this.requiredSizeInMemory = requiredSizeInMemory;
        this.currentExecutionTime = 0;
        this.currentTimeSlice = 0;
        this.inDisk = false;
    }

    public String getProcessName() {
        return processName;
    }

    public PCB getProcessControlBlock() {
        return processControlBlock;
    }

    public int getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public int getCurrentExecutionTime() {
        return currentExecutionTime;
    }

    public void setCurrentExecutionTime(int currentExecutionTime) {
        this.currentExecutionTime = currentExecutionTime;
    }

    public boolean isInDisk() {
        return inDisk;
    }

    public void setInDisk(boolean inDisk) {
        this.inDisk = inDisk;
    }

    public int getRequiredSizeInMemory() {
        return requiredSizeInMemory;
    }

    public int getCurrentTimeSlice() {
        return currentTimeSlice;
    }

    public void setCurrentTimeSlice(int currentTimeSlice) {
        this.currentTimeSlice = currentTimeSlice;
    }
}
