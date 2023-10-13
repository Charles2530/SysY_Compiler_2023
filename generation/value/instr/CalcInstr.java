package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class CalcInstr extends Instr {
    public CalcInstr(String name, String instrType, Value op1, Value op2) {
        super(new VarType(32), name, instrType);
        operands.add(op1);
        operands.add(op2);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + operands.get(0).getName() +
                ", " + operands.get(1).getName();
    }
}
