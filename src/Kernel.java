import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Kernel {

    static Object[] memory = new Object[40];
    static int currentMemorySpace = memory.length;
    static int placeholder = 0;
    static Semaphore fileMutex = new Semaphore(1);
    static Semaphore userInputMutex = new Semaphore(1);
    static Semaphore userOutputMutex = new Semaphore(1);
    static ArrayList<Process> processes = new ArrayList<>();
    static Disk disk = new Disk();
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
                PCB pcb = new PCB(processID, placeholder + 8, placeholder + 8 + instructions.size());
                memory[placeholder++] = processID;
                currentMemorySpace--;
                memory[placeholder++] = pcb.getProcessState();
                currentMemorySpace--;
                memory[placeholder++] = pcb.getProgramCounter();
                currentMemorySpace--;
                memory[placeholder++] = pcb.getMinimumInMemory();
                currentMemorySpace--;
                memory[placeholder++] = pcb.getMaximumInMemory();
                currentMemorySpace--;
                memory[placeholder++] = new Variable();
                currentMemorySpace--;
                memory[placeholder++] = new Variable();
                currentMemorySpace--;
                memory[placeholder++] = new Variable();
                currentMemorySpace--;
                for(Instruction e : instructions){
                    memory[placeholder++] = e;
                    currentMemorySpace--;
                }
                Process process = new Process("Program_" + processID, pcb, instructions.size(), instructions.size() + 8);
                process.getProcessControlBlock().setProcessState(ProcessState.READY);
                Scheduler.getReadyQueue().add(process);
                processes.add(process);
            }else {
                Process p = null;
                for(int i = 0; i < placeholder; i += 8){
                    p = processToSwap(i);
                }
                assert p != null;
                p.setInDisk(true);
                disk.getProcessesOnDisk().add(p);
                removeProcessFromMemory(p);
                disk.saveDisk();
                disk.write();
                allocateProcessToMemory(processID);
            }
        } catch (IOException ignored) {}
    }

    private static Process processToSwap(int processID){
        for(Process e : processes){
            if(e.getProcessControlBlock().getProcessID() == processID && !e.getProcessControlBlock().getProcessState().equals(ProcessState.RUNNING))
                return e;
        }
        return null;
    }

    private static void removeProcessFromMemory(Process p){
        for(int i = 0; i < memory.length; i++){
            if(memory[i] instanceof Integer && ((Integer)memory[i]) == p.getProcessControlBlock().getProcessID()){
                placeholder = i;
                break;
            }
        }
        if(placeholder + 8 + p.getInstructions().size() >= memory.length || memory[placeholder + 8 + p.getInstructions().size()] == null){
            for(int i = placeholder; i < placeholder + 8 + p.getInstructions().size(); i++){
                memory[i] = null;
                currentMemorySpace++;
            }
        }else if(memory[placeholder + 8 + p.getInstructions().size()] != null){
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            placeholder++;
            currentMemorySpace++;
            for(int i = 0; i < p.getInstructions().size(); i++){
                memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
                memory[placeholder + 8 + p.getInstructions().size()] = null;
                placeholder++;
                currentMemorySpace++;
            }
        }
    }
    private static void shifting(Process p)
    {
        for(int i = 0; i < memory.length; i++){
            if(memory[i] instanceof Integer && ((Integer)memory[i]) == p.getProcessControlBlock().getProcessID()){
                placeholder = i;
                break;
            }
        }
        for(int j =0 ; j< 3; j++){
            for(int i =placeholder; i<currentMemorySpace; i ++){

                memory[i+1] =memory[i];
                memory[i] = null;
            }
        }


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
