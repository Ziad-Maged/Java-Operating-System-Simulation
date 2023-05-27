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
        boolean running = true;
        int time = 0;
        Process p;
        while(running){
            if(time == 0){
                Kernel.allocateProcessToMemory(1);
            }
            if(time == 1){
                Kernel.allocateProcessToMemory(2);
            }
            if(time == 4){
                Kernel.allocateProcessToMemory(3);
            }
            processes.get(0).getProcessControlBlock().setProcessState(ProcessState.FINISHED);
            if(time == 0)
                Scheduler.reschedule();
            p = Scheduler.getCurrentRunningProcess();
            int c =0;
            for(int i = 0; i < memory.length; i++){
                if(memory[i] instanceof Integer && ((Integer)memory[i]) == p.getProcessControlBlock().getProcessID()){
                   c  = i;
                    break;
                }
            }
            int indexOfProgramCounter = c + 2;
            for(int j = (Integer)memory[indexOfProgramCounter] ; j < (c+ 8 + p.getInstructions().size()); j++){
                memory[indexOfProgramCounter] = j;
                if(memory[j] instanceof Instruction){
                    System.out.println("Instruction currently Executing: " + memory[j]);
                    Interpreter.parseCode((Instruction) memory[j]);
                    time++;
                }
                if(Scheduler.getCurrentRunningProcess().currentTimeSlice == 0 || Scheduler.getCurrentRunningProcess().getCurrentExecutionTime() == Scheduler.getCurrentRunningProcess().getTotalExecutionTime()){
                    if(Scheduler.getCurrentRunningProcess().getCurrentExecutionTime() == Scheduler.getCurrentRunningProcess().getTotalExecutionTime()){
                        Scheduler.getCurrentRunningProcess().getProcessControlBlock().setProcessState(ProcessState.FINISHED);
                        for (Process e : processes){
                            if(e.getProcessControlBlock().getProcessID() == Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessID()){
                                e.getProcessControlBlock().setProcessState(ProcessState.FINISHED);
                            }
                        }
                    }
                    Scheduler.reschedule();
                    break;
                }
            }
            if(isDone()){
                running = false;
            }
        }
        System.out.println(Arrays.deepToString(memory));
    }

    private static boolean isDone(){
        for(Process e : processes){
            if(!e.getProcessControlBlock().getProcessState().equals(ProcessState.FINISHED)){
                return false;
            }
        }
        return true;
    }



    public static void allocateProcessToMemory(int processID){
        try {
            BufferedReader br = new BufferedReader(new FileReader("Program_" + processID + ".txt"));
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
                for(Instruction e : instructions)
                    process.getInstructions().add(e);
                process.getProcessControlBlock().setProcessState(ProcessState.READY);
                Scheduler.getReadyQueue().add(process);
                processes.add(process);
            }else {
                Process p = null;
                for(int i = 0; i < placeholder; i += 8 + instructions.size()){
                    p = processToSwap((Integer)memory[i]);
                    if(p != null)
                        break;
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
            if(e.getProcessControlBlock().getProcessID() == processID && !e.getProcessControlBlock().getProcessState().equals(ProcessState.RUNNING) && !e.isInDisk())
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
            memory[placeholder] = (Integer)memory[placeholder] - 8 - p.getInstructions().size();
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            memory[placeholder] = (Integer)memory[placeholder] - 8 - p.getInstructions().size();
            placeholder++;
            currentMemorySpace++;
            memory[placeholder] = memory[placeholder + 8 + p.getInstructions().size()];
            memory[placeholder + 8 + p.getInstructions().size()] = null;
            memory[placeholder] = (Integer)memory[placeholder] - 8 - p.getInstructions().size();
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
