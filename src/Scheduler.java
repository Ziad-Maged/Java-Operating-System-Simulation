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
        //TODO
    }

    public static void reschedule(){
        //TODO
    }

    public static void unblockProcessOnResource(String resourceType){
        //TODO
    }
}
