package midend.generation.value.instr.basis;

import midend.generation.utils.IrType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.construction.user.Instr;

public class AllocaInstr extends Instr {
    private final IrType type;

    public AllocaInstr(String name, String instrType, IrType type) {
        super(new PointerType(type), name, instrType);
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " = alloca " + type;
    }
}
