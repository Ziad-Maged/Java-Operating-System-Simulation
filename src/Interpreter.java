import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
                        break;
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
            String fileName = "";
            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar1().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar2().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar3().getData().toString();
            }
            readFile(e, fileName);
        }else if(e.getInstruction().contains("writeFile")){
            String fileName = "";
            String data = "";
            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar1().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar2().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[1])){
                fileName = Scheduler.getCurrentRunningProcess().getVar3().getData().toString();
            }

            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[2])){
                data = Scheduler.getCurrentRunningProcess().getVar1().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[2])){
                data = Scheduler.getCurrentRunningProcess().getVar2().getData().toString();
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[2])){
                data = Scheduler.getCurrentRunningProcess().getVar3().getData().toString();
            }
            writeFile(fileName, data);
        }else if(e.getInstruction().contains("printFromTo")){
            int x = 0;
            int y = 0;
            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[1])){
                x = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar1().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[1])){
                x = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar2().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[1])){
                x = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar3().getData().toString());
            }

            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[2])){
                y = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar1().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[2])){
                y = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar2().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[2])){
                y = Integer.parseInt(Scheduler.getCurrentRunningProcess().getVar3().getData().toString());
            }
            printFromTo(x, y);
        }else if(e.getInstruction().contains("print")){
            if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals(instructionData[1])){
                print(Scheduler.getCurrentRunningProcess().getVar1().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals(instructionData[1])){
                print(Scheduler.getCurrentRunningProcess().getVar2().getData().toString());
            }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals(instructionData[1])){
                print(Scheduler.getCurrentRunningProcess().getVar3().getData().toString());
            }
        }

        Scheduler.getCurrentRunningProcess().currentTimeSlice++;
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
        StringBuilder wholeResult = new StringBuilder();
        for(int i = 2; i < data.length - 1; i++){
            wholeResult.append(data[i]).append(" ");
        }
        wholeResult.append(data[data.length - 1]);
        Variable var = new Variable(data[1], wholeResult.toString());
        if(Scheduler.getCurrentRunningProcess().getVar1().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar1(var);
        }else if(Scheduler.getCurrentRunningProcess().getVar2().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar2(var);
        }else if(Scheduler.getCurrentRunningProcess().getVar3().getVariableName().equals("null")){
            Scheduler.getCurrentRunningProcess().setVar3(var);
        }
        for(int i = 0; i < Kernel.memory.length; i++){
            if(Kernel.memory[i] instanceof Integer && Kernel.memory[i + 1] instanceof ProcessState && (Integer)Kernel.memory[i] == Scheduler.getCurrentRunningProcess().getProcessControlBlock().getProcessID()){
                if(((Variable)Kernel.memory[i + 5]).getVariableName().equals("null")){
                    Kernel.memory[i + 5] = var;
                    return;
                }else if(((Variable)Kernel.memory[i + 6]).getVariableName().equals("null")){
                    Kernel.memory[i + 6] = var;
                    return;
                }else if(((Variable)Kernel.memory[i + 7]).getVariableName().equals("null")){
                    Kernel.memory[i + 7] = var;
                    return;
                }
            }
        }
    }

    public static void semWait(String resourceType){
        switch (resourceType){
            case "file" ->{
                if(!Kernel.fileMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("file");
                    Scheduler.getCurrentRunningProcess().setCurrentTimeSlice(0);
                }
            }
            case "userInput" ->{
                if(!Kernel.userInputMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("userInput");
                    Scheduler.getCurrentRunningProcess().setCurrentTimeSlice(0);
                }
            }
            case "userOutput" ->{
                if(!Kernel.userOutputMutex.tryAcquire()){
                    Scheduler.blockCurrentProcessOnResource("userOutput");
                    Scheduler.getCurrentRunningProcess().setCurrentTimeSlice(0);
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
        try{
            FileWriter br = new FileWriter(fileName + ".txt");
            br.write(data);
            br.close();
        }catch(IOException ignored){}
    }

    public static void readFile(Instruction e, String fileName){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName + ".txt"));
            String line = br.readLine();
            e.setResult(line);
//            ArrayList<String>content = new ArrayList<>();
            while(line!=null){
//                content.add(line);
                line = br.readLine();
                e.setResult(e.getResult() + " " + line);
            }
            br.close();
        }catch(IOException ignored){}
    }

}
