public class Interpreter {

    public static void print(String s){
        System.out.println(s);
    }

    public static void semWait(String resourceType){
        switch(resourceType){
            case "file" ->{
                if(!Kernel.fileMutex.tryAcquire())
                    return;
            }
            case "userInput" -> {
                if(!Kernel.inputMutex.tryAcquire())
                    return;
            }
            case "userOutput" -> {
                if(!Kernel.outputMutex.tryAcquire())
                    return;
            }
        }
    }

    public static void semSignal(String resourceType){
        switch(resourceType){
            case "file" ->{
                Kernel.fileMutex.release();
            }
            case "userInput" ->{
                Kernel.inputMutex.release();
            }
            case "userOutput" ->{
                Kernel.outputMutex.release();
            }
        }
    }

}
