package midend.generation.value.instr;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;

public class JumpInstr extends Instr {
    public JumpInstr(String name, BasicBlock target) {
        super(new VarType(0), name, "jump");
        addOperand(target);
    }

    @Override
    public String toString() {
        return "br label %" + operands.get(0).getName();
    }
}
