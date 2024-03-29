import java.io.Serializable;
import java.util.ArrayList;

public class Process implements Serializable {
    private final String processName;
    private final PCB processControlBlock;
    private final int totalExecutionTime;
    private int currentExecutionTime;
    private boolean inDisk;
    private final int requiredSizeInMemory;
    public int currentTimeSlice;
    private Variable var1 = new Variable();
    private Variable var2 = new Variable();
    private Variable var3 = new Variable();
    private final ArrayList<Instruction> instructions;

    public Process(String processName, PCB processControlBlock, int totalExecutionTime, int requiredSizeInMemory) {
        this.processName = processName;
        this.processControlBlock = processControlBlock;
        this.totalExecutionTime = totalExecutionTime;
        this.requiredSizeInMemory = requiredSizeInMemory;
        this.currentExecutionTime = 0;
        this.currentTimeSlice = 2;
        this.inDisk = false;
        instructions = new ArrayList<>();
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

    public Variable getVar1() {
        return var1;
    }

    public void setVar1(Variable var1) {
        this.var1 = var1;
    }

    public Variable getVar2() {
        return var2;
    }

    public void setVar2(Variable va2) {
        this.var2 = va2;
    }

    public Variable getVar3() {
        return var3;
    }

    public void setVar3(Variable var3) {
        this.var3 = var3;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public  String toString(){
        return "Process Name: " + processName + "\n" + "Process Control Block: " + processControlBlock +"\n"+ "Total Execution Time: "+ totalExecutionTime + "\n"
                + "Current Execution Time: "+ currentExecutionTime + "\n" + "Size in Memory: "+requiredSizeInMemory +"\n" + "Time Slice: " + currentTimeSlice;
    }

    public boolean equals(Process process){
        return processControlBlock.equals(process.getProcessControlBlock());
    }
}
