package generation.value.instr;

import generation.value.Value;
import generation.value.construction.Instr;

public class RetInstr extends Instr {
    public RetInstr(String name, String instrType) {
        super(name, instrType);
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
