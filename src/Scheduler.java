import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    private final static Queue<Process> readyQueue = new LinkedList<>();
    private final static Queue<Process> generalBlockedQueue = new LinkedList<>();
    private final static Queue<Process> fileBlockedQueue = new LinkedList<>();
    private final static Queue<Process> userInputBlockedQueue = new LinkedList<>();
    private final static Queue<Process> userOutputBlockedQueue = new LinkedList<>();
    private static Process currentRunningProcess;
    private static final int quantum = 2;

    public static Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public static int getQuantum() {
        return quantum;
    }

    public static Process getCurrentRunningProcess() {
        return currentRunningProcess;
    }

    public static void blockCurrentProcessOnResource(String resourceType){
        currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.BLOCKED);
        switch (resourceType){
            case "file" ->
                fileBlockedQueue.add(currentRunningProcess);
            case "userInput" ->
                userInputBlockedQueue.add(currentRunningProcess);
            case "userOutput" ->
                userOutputBlockedQueue.add(currentRunningProcess);
        }
        generalBlockedQueue.add(currentRunningProcess);
        Kernel.blockCurrentProcess();
    }

    public static void reschedule(){
        if (currentRunningProcess != null) {
            if(currentRunningProcess.getCurrentExecutionTime() >= currentRunningProcess.getTotalExecutionTime()){
                currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.FINISHED);
                Kernel.finishCurrentProcess();
                if(readyQueue.isEmpty())
                    return;
            }
            if (!currentRunningProcess.getProcessControlBlock().getProcessState().equals(ProcessState.BLOCKED) && !currentRunningProcess.getProcessControlBlock().getProcessState().equals(ProcessState.FINISHED)) {
                readyQueue.add(currentRunningProcess);
                currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.READY);
                for(int i = 0; i < Kernel.memory.length; i++){
                    if(Kernel.memory[i] instanceof Integer && Kernel.memory[i + 1] instanceof ProcessState && (Integer)Kernel.memory[i] == currentRunningProcess.getProcessControlBlock().getProcessID()){
                        Kernel.memory[i + 1] = ProcessState.READY;
                        break;
                    }
                }
            }
        }
        currentRunningProcess = readyQueue.remove();
        currentRunningProcess.currentTimeSlice = 0;
        currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.RUNNING);
        for(Process e : Kernel.processes){
            if(e.getProcessControlBlock().getProcessID() == currentRunningProcess.getProcessControlBlock().getProcessID()){
                e.getProcessControlBlock().setProcessState(ProcessState.RUNNING);
                break;
            }
        }
        if(currentRunningProcess.isInDisk()){
            currentRunningProcess.setInDisk(false);
            Kernel.swapForCurrentProcess();
        }
        System.out.println("Ready Queue: " + readyQueue);
        System.out.println("General Blocked Queue: " + generalBlockedQueue);
        System.out.println("File Blocked Queue: " + fileBlockedQueue);
        System.out.println("UserInput Blocked Queue: " + userInputBlockedQueue);
        System.out.println("UserOutput Blocked Queue: " + userOutputBlockedQueue);
        System.out.println();
    }

    public static void updateProcessContent(Process e){
        for(Process p : readyQueue){
            if(p.getProcessControlBlock().getProcessID() == e.getProcessControlBlock().getProcessID()){
                p.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter());
                p.getProcessControlBlock().setMinimumInMemory(e.getProcessControlBlock().getMinimumInMemory());
                p.getProcessControlBlock().setMaximumInMemory(e.getProcessControlBlock().getMaximumInMemory());
                return;
            }
        }
        for(Process p : generalBlockedQueue){
            if(p.getProcessControlBlock().getProcessID() == e.getProcessControlBlock().getProcessID()){
                p.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter());
                p.getProcessControlBlock().setMinimumInMemory(e.getProcessControlBlock().getMinimumInMemory());
                p.getProcessControlBlock().setMaximumInMemory(e.getProcessControlBlock().getMaximumInMemory());
                break;
            }
        }
        for(Process p : fileBlockedQueue){
            if(p.getProcessControlBlock().getProcessID() == e.getProcessControlBlock().getProcessID()){
                p.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter());
                p.getProcessControlBlock().setMinimumInMemory(e.getProcessControlBlock().getMinimumInMemory());
                p.getProcessControlBlock().setMaximumInMemory(e.getProcessControlBlock().getMaximumInMemory());
                return;
            }
        }
        for(Process p : userInputBlockedQueue){
            if(p.getProcessControlBlock().getProcessID() == e.getProcessControlBlock().getProcessID()){
                p.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter());
                p.getProcessControlBlock().setMinimumInMemory(e.getProcessControlBlock().getMinimumInMemory());
                p.getProcessControlBlock().setMaximumInMemory(e.getProcessControlBlock().getMaximumInMemory());
                return;
            }
        }
        for(Process p : userOutputBlockedQueue){
            if(p.getProcessControlBlock().getProcessID() == e.getProcessControlBlock().getProcessID()){
                p.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter());
                p.getProcessControlBlock().setMinimumInMemory(e.getProcessControlBlock().getMinimumInMemory());
                p.getProcessControlBlock().setMaximumInMemory(e.getProcessControlBlock().getMaximumInMemory());
                return;
            }
        }
    }

    public static void unblockProcessOnResource(String resourceType){
        Process temp = null;
        switch (resourceType){
            case "file" ->{
                if(!fileBlockedQueue.isEmpty())
                    temp = fileBlockedQueue.remove();
            }
            case "userInput" ->{
                if(!userInputBlockedQueue.isEmpty())
                    temp = userInputBlockedQueue.remove();
            }
            case "userOutput" ->{
                if(!userOutputBlockedQueue.isEmpty())
                    temp = userOutputBlockedQueue.remove();
            }
        }
        if(temp != null){
            for(Process e : generalBlockedQueue){
                if(e.equals(temp)){
                    generalBlockedQueue.remove(e);
                    break;
                }
            }
            temp.getProcessControlBlock().setProcessState(ProcessState.READY);
            readyQueue.add(temp);
        }
    }

    public static void updateProcessToDisk(Process p){
        for(Process e : readyQueue){
            if(e.equals(p)){
                e.setInDisk(true);
                return;
            }
        }
        for(Process e : generalBlockedQueue){
            if(e.equals(p)){
                e.setInDisk(true);
                break;
            }
        }
        for(Process e : fileBlockedQueue){
            if(e.equals(p)){
                e.setInDisk(true);
                return;
            }
        }
        for(Process e : userInputBlockedQueue){
            if(e.equals(p)){
                e.setInDisk(true);
                return;
            }
        }
        for(Process e : userOutputBlockedQueue){
            if(e.equals(p)){
                e.setInDisk(true);
                return;
            }
        }
    }
}
