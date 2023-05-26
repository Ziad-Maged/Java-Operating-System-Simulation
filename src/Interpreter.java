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

    public static void assign(String code){
        //TODO
    }

    public static void semWait(String resourceType){
        //TODO LATER
    }

    public static void semSignal(String resourceType){
        //TODO LATER
    }

    public static void writeFile(String fileName, String data){
        //TODO LATER
    }

    public static void readFile(Instruction e, String fileName){
        //TODO LATER
    }

}
