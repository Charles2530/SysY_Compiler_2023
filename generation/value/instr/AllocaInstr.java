package generation.value.instr;

import generation.utils.IrType;
import generation.utils.irtype.PointerType;
import generation.value.construction.user.Instr;

public class AllocaInstr extends Instr {
    private IrType type;

    public AllocaInstr(String name, String instrType, IrType type) {
        super(new PointerType(type), name, instrType);
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " = alloca " + type;
    }
}
