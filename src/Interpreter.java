import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {

    public static void parseCode(Instruction e){
        if(e.getInstruction().contains("assign")){
            //TODO
        }else if(e.getInstruction().contains("semWait")){
            //TODO
        }else if(e.getInstruction().contains("semSignal")){
            //TODO
        }else if(e.getInstruction().contains("input")){
            //TODO
        }else if(e.getInstruction().contains("readFile")){
            //TODO
        }else if(e.getInstruction().contains("writeFile")){
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
            BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
            br.write(data);
            br.close();
        }catch(IOException ignored){}
    }

    public void readFile(Instruction e, String fileName) throws IOException{
        //TODO LATER
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
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
