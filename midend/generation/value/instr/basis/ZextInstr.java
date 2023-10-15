package midend.generation.value.instr.basis;

import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class ZextInstr extends Instr {
    private final IrType target;

    public ZextInstr(String name, String instrType, Value val, IrType target) {
        super(target, name, instrType);
        this.target = target;
        this.operands.add(val);
    }

    @Override
    public String toString() {
        return name + " = zext " + operands.get(0).getType() + " " +
                operands.get(0).getName() + " to " + target;
    }
}
