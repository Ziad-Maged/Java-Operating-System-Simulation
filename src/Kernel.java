import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Kernel {

    static Object[] memory = new Object[40];
    static int currentMemorySpace = memory.length;
    static int placeholder = 0;
    static Semaphore fileMutex = new Semaphore(1);
    static Semaphore userInputMutex = new Semaphore(1);
    static Semaphore userOutputMutex = new Semaphore(1);
    static ArrayList<Process> processes = new ArrayList<>();
    static ArrayList<Integer> processPCBIndices = new ArrayList<>();
    public static void startProgram(){
        //TODO Later
    }

    public static void allocateProcessToMemory(int processID){
        try {
            BufferedReader br = new BufferedReader(new FileReader("Program_" + processID));
            ArrayList<Instruction> instructions = new ArrayList<>();
            while (br.ready()){
                String s = br.readLine();
                String[] fullInstruction = s.split(" ");
                decodeInstruction(s, fullInstruction, instructions);
            }
            if(instructions.size() + 8 <= currentMemorySpace){
                //TODO
            }else {
                //TODO SWAPPING
            }
        } catch (IOException ignored) {}
    }

    private static void memoryToDisk(){
        //TODO
    }

    private static void diskToMemory(){
        //TODO
    }

    private static void decodeInstruction(String instruction, String[] split, ArrayList<Instruction> instructions){
        if(instruction.contains("assign") && instruction.contains("input")){
            instructions.add(new Instruction("input", false));
            instructions.add(new Instruction(split[0] + " " + split[1], true));
        }else if(instruction.contains("assign") && instruction.contains("readFile")){
            instructions.add(new Instruction(split[2] + " " + split[3], false));
            instructions.add(new Instruction(split[0] + " " + split[1], true));
        }else{
            instructions.add(new Instruction(instruction, false));
        }
    }

    public static void main(String[] args) {
        Kernel.startProgram();
    }

}
