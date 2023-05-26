import java.io.*;
import java.sql.Array;
import java.util.ArrayList;

public class Disk implements Serializable {

    ArrayList<Process> processesOnDisk;

    public Disk(){
        processesOnDisk = new ArrayList<>();
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

}
