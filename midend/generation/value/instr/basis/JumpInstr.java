package midend.generation.value.instr.basis;

import backend.generation.mips.asm.textsegment.mipsinstr.JtypeAsm;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * JumpInstr 用于生成 LLVM IR 中的 br 指令,
 * 继承于 Instr,主要用于生成无条件跳转指令
 * br label <dest>
 */
public class JumpInstr extends Instr {
    /**
     * target 是 br 指令的目标基本块
     * isAssemblerReduce 用于判断是否需要生成汇编代码,
     * 主要用于后端代码优化时去除连接块的不必要跳转语句
     */
    private BasicBlock target;
    private boolean isAssemblerReduce;

    public JumpInstr(BasicBlock target) {
        super(new VarType(0), "JumpInstr", "jump");
        addOperand(target);
        this.target = target;
        this.isAssemblerReduce = false;
    }

    public void setTarget(BasicBlock target) {
//        if (getTarget() != null &&
//                getBelongingBlock().getBlockOutBasicBlock().contains(getTarget())) {
//            ControlFlowGraph.addBlockOutBasicBlock(getBelongingBlock(), target,
//                    getBelongingBlock().getBlockOutBasicBlock().indexOf(getTarget()));
//            getBelongingBlock().getBlockOutBasicBlock().remove(getTarget());
//            ControlFlowGraph.addBlockIndBasicBlock(target, getBelongingBlock());
//            getTarget().getBlockIndBasicBlock().remove(getBelongingBlock());
//        }
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

//    @Override
//    public void setBelongingBlock(BasicBlock currentBasicBlock) {
//        super.setBelongingBlock(currentBasicBlock);
//        if (!getBelongingBlock().getBlockOutBasicBlock().contains(target) && target != null) {
//            ControlFlowGraph.addBlockIndBasicBlock(target, getBelongingBlock());
//            ControlFlowGraph.addBlockOutBasicBlock(getBelongingBlock(), target);
//        }
//    }

    public void setAssemblerReduce() {
        isAssemblerReduce = true;
    }

}
