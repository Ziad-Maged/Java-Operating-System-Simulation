import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Kernel {

    static Object[] memory = new Object[40];
    static int memorySize = 0;
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

    public static void allocateProcessToMemory(int processID) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("Program_" + processID));
        ArrayList<String> processUnParsedLines = new ArrayList<>();
        int minimum = memorySize + 5;
        int maximum = memorySize;
        int processProgramCounter = minimum + 5;
        ProcessState state = ProcessState.NEW;
        while(br.ready()){
            processUnParsedLines.add(br.readLine());
        }
        if(processUnParsedLines.size() + 5 < memory.length - memorySize){
            memory[memorySize++] = processID;
            memory[memorySize++] = state;
            memory[memorySize++] = processProgramCounter;
        }else {
            //TODO LATER
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Kernel.startProgram();
    }

}
