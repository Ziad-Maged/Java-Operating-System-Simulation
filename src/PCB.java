import java.io.Serializable;

public class PCB implements Serializable {

    private final int processID;
    private ProcessState processState;
    private int programCounter;
    private int minimumInMemory;
    private int maximumInMemory;

    public PCB(int processID, ProcessState processState, int programCounter, int minimumInMemory, int maximumInMemory){
        this.processID = processID;
        this.processState = processState;
        this.programCounter = programCounter;
        this.minimumInMemory = minimumInMemory;
        this.maximumInMemory = maximumInMemory;
    }

    public PCB(int processID, int programCounter, int minimumInMemory, int maximumInMemory){
        this(processID, ProcessState.READY, programCounter, minimumInMemory, maximumInMemory);
    }

    public PCB(int processID, int minimumInMemory, int maximumInMemory){
        this(processID, minimumInMemory, minimumInMemory, maximumInMemory);
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getProcessID() {
        return processID;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public int getMaximumInMemory() {
        return maximumInMemory;
    }

    public int getMinimumInMemory() {
        return minimumInMemory;
    }

    public void setMaximumInMemory(int maximumInMemory) {
        this.maximumInMemory = maximumInMemory;
    }

    public void setMinimumInMemory(int minimumInMemory) {
        this.minimumInMemory = minimumInMemory;
    }

    public String toString(){
        return "processID=" + processID + " processState=" + processState + " PC=" + programCounter
                + " minimumInMemory=" + minimumInMemory + " maximumInMemory=" + maximumInMemory;
    }

    public boolean equals(PCB pcb){
        return processID == pcb.getProcessID();
    }
}
