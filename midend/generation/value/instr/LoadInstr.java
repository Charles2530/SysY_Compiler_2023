package midend.generation.value.instr;

import midend.generation.utils.irtype.PointerType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

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
