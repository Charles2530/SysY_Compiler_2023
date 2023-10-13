package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class StoreInstr extends Instr {
    public StoreInstr(String name, String instrType, Value ans, Value res) {
        super(new VarType(0), name, instrType);
        addOperand(ans);
        addOperand(res);
    }

    @Override
    public String toString() {
        return "store " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getType()
                + "* " + operands.get(1).getName();
    }
}