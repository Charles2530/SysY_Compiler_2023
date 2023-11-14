package midend.generation.value.instr.basis;

import backend.generation.mips.asm.textsegment.mipsinstr.JtypeAsm;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

public class JumpInstr extends Instr {
    private BasicBlock target;
    private boolean isAssemblerReduce = false;

    public JumpInstr(BasicBlock target) {
        super(new VarType(0), "JumpInstr", "jump");
        addOperand(target);
        this.target = target;
    }

    public void setTarget(BasicBlock target) {
        operands.set(0, target);
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
        if (isAssemblerReduce) {
            return;
        }
        new JtypeAsm("j", operands.get(0).getName());
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        BasicBlock copyTarget = (BasicBlock) functionClone.getValue(this.getOperands().get(0));
        Instr instr = new JumpInstr(copyTarget);
        copyBlock.addInstr(instr);
        return instr;
    }

    public void setAssemblerReduce() {
        isAssemblerReduce = true;
    }
}
