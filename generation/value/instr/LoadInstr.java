package generation.value.instr;

import generation.utils.irtype.PointerType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class LoadInstr extends Instr {
    public LoadInstr(String name, String instrType, Value ptr) {
        super(((PointerType) ptr.getType()).getTarget(), name, instrType);
        addOperand(ptr);
    }

    @Override
    public String toString() {
        return name + " = load " + type + ", " +
                operands.get(0).getType() + " " + operands.get(0).getName();
    }
}
