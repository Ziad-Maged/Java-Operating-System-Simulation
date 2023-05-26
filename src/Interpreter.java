import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {

    public static void parseCode(Instruction e){
        String[] instructionData = e.getInstruction().split(" ");
        if(e.getInstruction().contains("assign")){
            if(e.isDependent()){
                int indexOfPreviousInstruction = 0;
                for(int i = 0; i < Kernel.memory.length; i++){
                    if(Kernel.memory[i] instanceof Instruction && Kernel.memory[i].equals(e)){
                        indexOfPreviousInstruction = i - 1;
                    }
                }
                String wholeIns = e.getInstruction() + " " + ((Instruction)Kernel.memory[indexOfPreviousInstruction]).getResult();
                assign(wholeIns);
            }else {
                assign(e.getInstruction());
            }
        }else if(e.getInstruction().contains("semWait")){
            semWait(instructionData[1]);
        }else if(e.getInstruction().contains("semSignal")){
            semSignal(instructionData[1]);
        }else if(e.getInstruction().contains("input")){
            input(e);
        }else if(e.getInstruction().contains("readFile")){
            readFile(e, instructionData[1]);
        }else if(e.getInstruction().contains("writeFile")){
            //TODO
        }else if(e.getInstruction().contains("printFromTo")){
            printFromTo(Integer.parseInt(instructionData[1]), Integer.parseInt(instructionData[2]));
        }else if(e.getInstruction().contains("print")){
            //TODO
        }

        Scheduler.getCurrentRunningProcess().currentTimeSlice--;
    }

    public static void print(String message){
        System.out.println(message);
    }

    public static void printFromTo(int a, int b){
        System.out.print("[ ");
        for(int i = a; i < b; i++){
            System.out.print(i + ", ");
        }
        System.out.println(b + " ]");
    }

    public static void input(Instruction e){
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter a value: ");
        e.setResult(sc.nextLine());
    }

    public static void assign(String code){
        String[] data = code.split(" ");
        Variable var = new Variable(data[1], data[2]);
        if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar1(var);
        }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar2(var);
        }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar3(var);
        }
    }

    public static void semWait(String resourceType){
        switch (resourceType){
            case "file" ->{
                if(!Kernel.fileMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("file");
                }
            }
            case "userInput" ->{
                if(!Kernel.userInputMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("userInput");
                }
            }
            case "userOutput" ->{
                if(!Kernel.userOutputMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("userOutput");
                }
            }
        }
    }

    public static void semSignal(String resourceType){
        switch(resourceType){
            case "file" -> {
                Kernel.fileMutex.release();
                Scheduler.unblockProcessOnResource("file");
            }
            case "userInput" ->{
                Kernel.userInputMutex.release();
                Scheduler.unblockProcessOnResource("userInput");
            }
            case "userOutput" ->{
                Kernel.userOutputMutex.release();
                Scheduler.unblockProcessOnResource("userOutput");
            }
        }
    }

    public static void writeFile(String fileName, String data) {
        //TODO LATER
        try{
            BufferedWriter br = new BufferedWriter(new FileWriter(fileName + ".txt"));
            br.write(data);
            br.close();
        }catch(IOException ignored){}
    }

    public static void readFile(Instruction e, String fileName){
        //TODO LATER
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName + ".txt"));
            String line = br.readLine();
            e.setResult(line);
            ArrayList<String>content = new ArrayList<>();
            while(line!=null){
                content.add(line);
                line = br.readLine();
                e.setResult(e.getResult() + " " + line);
            }
            br.close();
        }catch(IOException ignored){}
    }

}
