package generation.value.construction;

import generation.utils.IrType;
import generation.value.Value;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> operands;

    public User(IrType type, String name) {
        super(type, name);
        this.operands = new ArrayList<>();
    }

    public void addOperand(Value operand) {
        operands.add(operand);
        if (operand != null) {
            //operand.addUse(this);
        }
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }
}
