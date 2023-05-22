public class PCB {
    private final int processID;
    private ProcessState processState;
    private int programCounter;
    private final int minimum;
    private final int maximum;

    public PCB(int processID, int minimum, int maximum){
        this.processID = processID;
        processState = ProcessState.NEW;
        this.programCounter = this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getProcessID() {
        return processID;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }
}
