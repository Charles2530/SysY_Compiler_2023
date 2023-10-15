package midend.generation.value.instr.basis;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;

public class BrInstr extends Instr {
    public BrInstr(Value con, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(new VarType(0), "BrInstr", "br");
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
