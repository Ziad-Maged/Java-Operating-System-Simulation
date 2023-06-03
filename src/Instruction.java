import java.io.Serializable;

public class Instruction implements Serializable {
    private final String instruction;
    private final boolean dependent;
    private Object result;

    public Instruction(String instruction, boolean dependent){
        this.instruction = instruction;
        this.dependent = dependent;
    }

    public String getInstruction() {
        return instruction;
    }

    public boolean isDependent() {
        return dependent;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String toString(){
        StringBuilder stringResult = new StringBuilder(instruction + " " + ((dependent) ? "dependent result=" : "result="));
        if(this.result == null)
            stringResult.append("null");
        else {
            String[] instructionSet = result.toString().split(" ");
            for(int i = 0; i < instructionSet.length - 1; i++){
                stringResult.append(instructionSet[i]).append("_");
            }
            stringResult.append(instructionSet[instructionSet.length - 1]);
        }
        return stringResult.toString();
    }
}
