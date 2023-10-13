package generation.value.instr;

import generation.value.construction.Instr;

public class AllocaInstr extends Instr {
    private String type;

    public AllocaInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        return name + " = alloca " + type;
    }
}
