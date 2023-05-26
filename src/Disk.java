import java.io.*;
import java.util.ArrayList;

public class Disk implements Serializable {

    private final ArrayList<Process> processesOnDisk;

    public Disk(){
        processesOnDisk = new ArrayList<>();
    }

    public ArrayList<Process> getProcessesOnDisk() {
        return processesOnDisk;
    }

    public void saveDisk(){
        try {
            FileOutputStream fileOut = new FileOutputStream("Disk.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString(){
        StringBuilder string = new StringBuilder("");
        for(Process e : processesOnDisk){
            string.append(e.getProcessName()).append("\n").append(e.toString()).append("\n");
        }
        return string.toString();
    }

    public void write(){
        try {
            PrintWriter writer = new PrintWriter(new File("DISK.txt"));
            for(Process e : processesOnDisk){
                writer.println(e.toString());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
