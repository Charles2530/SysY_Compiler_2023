package generation.value.instr;

import generation.value.construction.Instr;

public class LoadInstr extends Instr {
    public LoadInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        return name + " = load " + operands.get(0).getType() + ", " +
                operands.get(0).getType() + "* " + operands.get(0).getName();
    }
}
