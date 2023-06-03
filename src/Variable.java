import java.io.Serializable;

public class Variable implements Serializable {

    private final String variableName;
    private final Object data;

    public Variable(String variableName, Object data) {
        this.variableName = variableName;
        this.data = data;
    }

    public Variable(){
        variableName = "null";
        data = null;
    }

    public String getVariableName(){
        return variableName;
    }

    public Object getData() {
        return data;
    }

    public String toString(){
        return variableName + " = " + (( data!=null )? data.toString():"null");
    }
}
