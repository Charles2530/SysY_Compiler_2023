package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class IcmpInstr extends Instr {
    public IcmpInstr(String name, String instrType, Value ans, Value res) {
        super(new VarType(1), name, instrType);
        operands.add(ans);
        operands.add(res);
    }

    @Override
    public String toString() {
        return name + " = icmp " + instrType + " " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }
}
