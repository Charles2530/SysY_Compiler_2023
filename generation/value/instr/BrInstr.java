package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.BasicBlock;
import generation.value.construction.user.Instr;

public class BrInstr extends Instr {
    public BrInstr(String name, String instrType, Value con,
                   BasicBlock thenBlock, BasicBlock elseBlock) {
        super(new VarType(0), name, instrType);
        addOperand(con);
        addOperand(thenBlock);
        addOperand(elseBlock);
    }

    @Override
    public String toString() {
        return "br i1 " + operands.get(0).getName() + ", label %" +
                operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }
}
