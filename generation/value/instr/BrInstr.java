package generation.value.instr;

import generation.value.construction.Instr;

public class BrInstr extends Instr {
    public BrInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        return "br i1 " + operands.get(0).getName() + ", label %" +
                operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }
}
