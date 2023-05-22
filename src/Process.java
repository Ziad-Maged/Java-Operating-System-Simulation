import java.io.Serializable;

public class Process implements Serializable {
    private final String processName;
    private final PCB processControlBlock;

    public Process(String processName, PCB processControlBlock) {
        this.processName = processName;
        this.processControlBlock = processControlBlock;
    }
}
