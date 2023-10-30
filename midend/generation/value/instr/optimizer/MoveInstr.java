package midend.generation.value.instr.optimizer;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class MoveInstr extends Instr {
    public MoveInstr(String name, Value from, Value to) {
        super(new VarType(0), name, "move");
        addOperand(from);
        addOperand(to);
    }

    @Override
    public String toString() {
        return "move " + operands.get(1).getType() + " " +
                operands.get(1).getName() + ", " + operands.get(0).getName();
    }

    /*TODO:need override*/
    @Override
    public void generateAssembly() {
        super.generateAssembly();
    }
}
