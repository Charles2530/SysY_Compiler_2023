package generation.value.instr;

import generation.value.construction.Instr;

public class StoreInstr extends Instr {
    public StoreInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        return "store " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getType()
                + "* " + operands.get(1).getName();
    }
}
