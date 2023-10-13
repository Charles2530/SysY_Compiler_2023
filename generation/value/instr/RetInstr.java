package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class RetInstr extends Instr {
    public RetInstr(String name, String instrType, Value retValue) {
        super(new VarType(0), name, instrType);
        if (retValue != null) {
            addOperand(retValue);
        }
    }

    @Override
    public String toString() {
        Value retValue = operands.isEmpty() ? null : operands.get(0);
        if (retValue != null) {
            return "ret " + retValue.getType() + " " + retValue.getName();
        }
        return "ret void";
    }
}
