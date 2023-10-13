package generation.value.instr;

import generation.value.construction.Instr;

public class IcmpInstr extends Instr {
    public IcmpInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        return name + " = icmp " + instrType + " " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }
}
