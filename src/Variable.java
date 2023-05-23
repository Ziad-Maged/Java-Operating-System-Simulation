public class Variable {

    private final String variableName;
    private Object variableData;


    public Variable(String variableName, Object variableData) {
        this.variableName = variableName;
        this.variableData = variableData;
    }

    public Object getVariableData() {
        return variableData;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableData(Object variableData) {
        this.variableData = variableData;
    }
}
