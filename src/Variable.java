import java.io.Serializable;

public class Variable implements Serializable {

    private String variableName;
    private Object data;

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

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString(){
        return "variableName=" + variableName + " data=" + (( data!=null )? data.toString():"null");
    }
}
