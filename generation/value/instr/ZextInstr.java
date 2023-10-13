package generation.value.instr;

import generation.utils.IrType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class ZextInstr extends Instr {
    private IrType target;

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
