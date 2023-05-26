import java.util.Scanner;

public class Interpreter {

    public static void parseCode(Instruction e){
        //TODO LATER
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
        //TODO
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
            }
            case "userInput" ->{
                Kernel.userInputMutex.release();
            }
            case "userOutput" ->{
                Kernel.userOutputMutex.release();
            }
        }
    }

    public static void writeFile(String fileName, String data){
        //TODO LATER
    }

    public static void readFile(Instruction e, String fileName){
        //TODO LATER
    }

}
