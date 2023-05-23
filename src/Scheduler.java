import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {

    private static final int quantum = 2;
    private static final Queue<Integer> generalBlockedProcesses = new LinkedList<>();

    public static int getQuantum() {
        return quantum;
    }

    public static Queue<Integer> getGeneralBlockedProcesses() {
        return generalBlockedProcesses;
    }
}
