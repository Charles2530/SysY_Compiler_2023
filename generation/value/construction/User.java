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

    public void addOperand(Value value, Integer...cnt) {
        int times = 1;
        if (cnt.length != 0) {
            times = cnt[0];
        }
        for (int i = 0; i < times; i++) {
            operands.add(value);
            if (value != null) {
                //value.addUse(this);
            }
        }
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }
}
