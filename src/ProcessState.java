import java.io.Serializable;

public enum ProcessState implements Serializable {
    READY,
    RUNNING,
    BLOCKED,
    FINISHED
}
