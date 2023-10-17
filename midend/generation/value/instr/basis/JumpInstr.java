package midend.generation.value.instr.basis;

import backend.mips.asm.textsegment.mipsinstr.JtypeAsm;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;

public class JumpInstr extends Instr {
    BasicBlock target;

    public JumpInstr(BasicBlock target) {
        super(new VarType(0), "JumpInstr", "jump");
        addOperand(target);
        this.target = target;
    }

    public BasicBlock getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "br label %" + operands.get(0).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        new JtypeAsm("j", operands.get(0).getName());
    }
}
