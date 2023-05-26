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

    public static void setCurrentRunningProcess(Process currentRunningProcess) {
        Scheduler.currentRunningProcess = currentRunningProcess;
    }

    public static void blockCurrentProcessOnResource(String resourceType){
        currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.BLOCKED);
        switch (resourceType){
            case "file" ->{
                fileBlockedQueue.add(currentRunningProcess);
            }
            case "userInput" ->{
                userInputBlockedQueue.add(currentRunningProcess);
            }
            case "userOutput" ->{
                userOutputBlockedQueue.add(currentRunningProcess);
            }
        }
        generalBlockedQueue.add(currentRunningProcess);
    }

    public static void reschedule(){
        if(!currentRunningProcess.getProcessControlBlock().getProcessState().equals(ProcessState.BLOCKED)){
            readyQueue.add(currentRunningProcess);
            currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.READY);
        }
        currentRunningProcess = readyQueue.remove();
        currentRunningProcess.getProcessControlBlock().setProcessState(ProcessState.RUNNING);
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
}
