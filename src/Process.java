import java.io.Serializable;

/**
 * May Convert to a Record is all the instance variables are final
 * */
public class Process implements Serializable {
    private final String processName;
    private final PCB processControlBlock;

    public Process(String processName, PCB processControlBlock) {
        this.processName = processName;
        this.processControlBlock = processControlBlock;
    }

    public String getProcessName() {
        return processName;
    }

    public PCB getProcessControlBlock() {
        return processControlBlock;
    }
}
