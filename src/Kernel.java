import java.util.concurrent.Semaphore;

public class Kernel {

    static Object[] memory = new Object[40];
    static Semaphore outputMutex = new Semaphore(1);
    static Semaphore inputMutex = new Semaphore(1);
    static Semaphore fileMutex = new Semaphore(1);

    public static void startProgram(){
        int time = 0;
        boolean running = true;
        while (running){
            //TODO LATER
            time++;
            running = false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Kernel.startProgram();
    }

}
