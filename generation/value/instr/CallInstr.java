package generation.value.instr;

import generation.value.Value;
import generation.value.construction.user.Function;
import generation.value.construction.user.Instr;

import java.util.ArrayList;

public class CallInstr extends Instr {
    public CallInstr(String name, String instrType,
                     Function targetFunc, ArrayList<Value> paramList) {
        super(targetFunc.getType(), name, instrType);
        addOperand(targetFunc);
        for (Value param : paramList) {
            addOperand(param);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type.isVoid()) {
            sb.append("call void ");
        } else {
            sb.append(name).append(" = call ").append(type).append(" ");
        }
        sb.append(operands.get(0).getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getType()).append(" ").append(operands.get(i).getName());
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}