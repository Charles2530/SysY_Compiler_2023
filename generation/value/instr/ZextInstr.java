package generation.value.instr;

import generation.utils.IrType;
import generation.value.construction.Instr;

public class ZextInstr extends Instr {
    private IrType target;

    public ZextInstr(String name, String instrType, IrType target) {
        super(name, instrType);
        this.target = target;
    }

    @Override
    public String toString() {
        return name + " = zext " + operands.get(0).getType() + " " +
                operands.get(0).getName() + " to " + target;
    }
}
