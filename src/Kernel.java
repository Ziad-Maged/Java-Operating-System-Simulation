import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
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
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Process 1 Arrival Time: ");
        int processOneArrivalTime = sc.nextInt();
        System.out.print("Enter Process 2 Arrival Time: ");
        int processTwoArrivalTime = sc.nextInt();
        System.out.print("Enter Process 3 Arrival Time: ");
        int processThreeArrivalTime = sc.nextInt();
        int time = 0;
        Process p = null;
        int indexOfProgramCounter = 0;
        while(true){
            if(time == processOneArrivalTime){
                Kernel.allocateProcessToMemory(1);
            }
            if(time == processTwoArrivalTime){
                Kernel.allocateProcessToMemory(2);
            }
            if(time == processThreeArrivalTime){
                Kernel.allocateProcessToMemory(3);
            }
            if(Scheduler.getCurrentRunningProcess() == null || Scheduler.getCurrentRunningProcess().getCurrentTimeSlice() >= Scheduler.getQuantum() || memory[indexOfProgramCounter - 1].equals(ProcessState.BLOCKED) || Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProgramCounter() > Scheduler.getCurrentRunningProcess().getProcessControlBlock().getMaximumInMemory()){
                Scheduler.reschedule();
                if(isDone()){
                    break;
                }
                p = Scheduler.getCurrentRunningProcess();
                int c =0;
                for(int i = 0; i < memory.length; i++){
                    if(memory[i] instanceof Integer && ((Integer)memory[i]) == p.getProcessControlBlock().getProcessID()){
                        c  = i;
                        break;
                    }
                }
                indexOfProgramCounter = c + 2;
            }
            memory[indexOfProgramCounter - 1] = ProcessState.RUNNING;
            System.out.println("Time = " + time);
            System.out.println("Process " + memory[indexOfProgramCounter - 2] + " Currently Running.");
            Interpreter.parseCode((Instruction) memory[(Integer)memory[indexOfProgramCounter]]);
            System.out.println("Current Instruction executing is " + ((Instruction) memory[(Integer)memory[indexOfProgramCounter]]).getInstruction());
            if(!Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessState().equals(ProcessState.BLOCKED)){
                for(Process e : processes){
                    if(e.equals(p)){
                        int indexToSet = (Integer)memory[indexOfProgramCounter] - (Integer) memory[indexOfProgramCounter + 1];
                        e.getInstructions().set(indexToSet, (Instruction) memory[(Integer) memory[indexOfProgramCounter]]);
                        Scheduler.getCurrentRunningProcess().getInstructions().set(indexToSet, (Instruction) memory[(Integer) memory[indexOfProgramCounter]]);
                        if(!Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessState().equals(ProcessState.BLOCKED))
                            e.getProcessControlBlock().setProgramCounter(e.getProcessControlBlock().getProgramCounter() + 1);
                        e.setCurrentExecutionTime(e.getCurrentExecutionTime() + 1);
                        break;
                    }
                }
                if(!Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessState().equals(ProcessState.BLOCKED))
                    memory[indexOfProgramCounter] = (Integer)memory[indexOfProgramCounter] + 1;
            }else {
                memory[indexOfProgramCounter - 1] = ProcessState.BLOCKED;
            }
            time++;
            System.out.println(Arrays.deepToString(memory));
            System.out.println();
        }
    }

    public static void swapForCurrentProcess(){
        for(Process e : disk.getProcessesOnDisk()){
            if(e.equals(Scheduler.getCurrentRunningProcess())){
                e.setInDisk(false);
                disk.getProcessesOnDisk().remove(e);
                break;
            }
        }
        Process p = null;
        for(int i = 0; i < placeholder; i++){
            if(!(memory[i] instanceof Integer) || !(memory[i+1] instanceof ProcessState))
                continue;
            assert memory[i] instanceof Integer;
            p = processToSwap((Integer)memory[i]);
            if(p != null)
                break;
        }
        assert p != null;
        p.setInDisk(true);
        for(Process e : processes){
            if(e.equals(p))
                e.setInDisk(true);
        }
        Scheduler.updateProcessToDisk(p);
        disk.getProcessesOnDisk().add(p);
        removeProcessFromMemory(p);
        disk.saveDisk();
        disk.write();
        for(Process e : processes){
            if(e.equals(Scheduler.getCurrentRunningProcess())){
                e.setInDisk(false);
                if(currentMemorySpace < e.getRequiredSizeInMemory()){
                    swapForCurrentProcess();
                }else {
                    int pcAmount = e.getProcessControlBlock().getProgramCounter() - e.getProcessControlBlock().getMinimumInMemory();
                    memory[placeholder] = e.getProcessControlBlock().getProcessID();
                    memory[placeholder + 1] = ProcessState.RUNNING;
                    memory[placeholder + 2] = placeholder + 8 + pcAmount;
                    memory[placeholder + 3] = placeholder + 8;
                    memory[placeholder + 4] = placeholder + 8 + e.getInstructions().size() - 1;
                    memory[placeholder + 5] = e.getVar1();
                    memory[placeholder + 6] = e.getVar2();
                    memory[placeholder + 7] = e.getVar3();
                    placeholder += 8;
                    currentMemorySpace -= 8;
                    for(Instruction i : e.getInstructions()){
                        memory[placeholder++] = i;
                        currentMemorySpace--;
                    }
                }
                return;
            }
        }
    }

    private static boolean isDone(){
        for(Process e : processes){
            if(!e.getProcessControlBlock().getProcessState().equals(ProcessState.FINISHED)){
                return false;
            }
        }
        return true;
    }

    public static void finishCurrentProcess(){
        for(Process e : processes){
            if(e.equals(Scheduler.getCurrentRunningProcess())){
                e.getProcessControlBlock().setProcessState(ProcessState.FINISHED);
                for(int i = 0; i < memory.length; i++){
                    if(memory[i] != null && memory[i] instanceof Integer && memory[i + 1] instanceof ProcessState && (Integer)memory[i] == e.getProcessControlBlock().getProcessID()){
                        memory[i + 1] = ProcessState.FINISHED;
                        return;
                    }
                }
            }
        }
    }

    public static void blockCurrentProcess(){
        for(int i = 0; i < memory.length; i++){
            if(memory[i] != null && memory[i] instanceof Integer && memory[i + 1] instanceof ProcessState && (Integer)memory[i] == Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessID()){
                memory[i + 1] = ProcessState.BLOCKED;
                break;
            }
        }
        for(Process e : processes){
            if(e.equals(Scheduler.getCurrentRunningProcess())){
                e.getProcessControlBlock().setProcessState(ProcessState.BLOCKED);
            }
        }
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
                PCB pcb = new PCB(processID, placeholder + 8, placeholder + 8 + instructions.size() - 1);
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
                Scheduler.getReadyQueue().add(process);
                processes.add(process);
            }else {
                Process p = null;
                for(int i = 0; i < placeholder; i++){
                    if(!(memory[i] instanceof Integer) || !(memory[i+1] instanceof ProcessState))
                        continue;
                    assert memory[i] instanceof Integer;
                    p = processToSwap((Integer)memory[i]);
                    if(p != null)
                        break;
                }
                assert p != null;
                p.setInDisk(true);
                for(Process e : processes){
                    if(e.equals(p))
                        e.setInDisk(true);
                }
                Scheduler.updateProcessToDisk(p);
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
            int index = placeholder + 8 + p.getInstructions().size();
            while(index < memory.length){
                if(memory[index] instanceof Integer && memory[index + 1] instanceof ProcessState){
                    memory[index + 2] = (Integer)memory[index + 2] - 8 - p.getInstructions().size();
                    memory[index + 3] = (Integer)memory[index + 3] - 8 - p.getInstructions().size();
                    memory[index + 4] = (Integer)memory[index + 4] - 8 - p.getInstructions().size();
                    for(Process e : processes){
                        if(e.getProcessControlBlock().getProcessID() == (Integer)memory[index]){
                            e.getProcessControlBlock().setProgramCounter((Integer) memory[index + 2]);
                            e.getProcessControlBlock().setMinimumInMemory((Integer)memory[index + 3]);
                            e.getProcessControlBlock().setMaximumInMemory((Integer)memory[index + 4]);
                            Scheduler.updateProcessContent(e);
                        }
                    }
                }
                memory[placeholder] = memory[index];
                memory[index] = null;
                placeholder++;
                currentMemorySpace++;
                index++;
            }
            while (memory[placeholder - 1] == null){
                placeholder--;
            }
        }
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