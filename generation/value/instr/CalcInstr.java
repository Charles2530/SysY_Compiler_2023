package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class CalcInstr extends Instr {
    public CalcInstr(String name, String instrType, Value op1, Value op2) {
        super(new VarType(32), name, instrType);
        addOperand(op1);
        addOperand(op2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" i32 ")
                .append(operands.get(0).getName()).append(
                        ", ").append(operands.get(1).getName());
        return sb.toString();
    }
}
