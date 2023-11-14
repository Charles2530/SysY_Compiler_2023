package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.BtypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.JtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * BrInstr 用于生成 LLVM IR 中的 br 指令,
 * 继承于 Instr,主要用于生成条件跳转指令
 */
public class BrInstr extends Instr {
    /**
     * con 是 br 指令的条件基本块
     * thenBlock 是 br 指令的真分支基本块
     * elseBlock 是 br 指令的假分支基本块
     * br i1 <cond>, label <iftrue>, label <iffalse>
     */
    private BasicBlock thenBlock;
    private BasicBlock elseBlock;

    public BrInstr(Value con, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(new VarType(0), "BrInstr", "br");
        addOperand(con);
        this.thenBlock = thenBlock;
        addOperand(thenBlock);
        this.elseBlock = elseBlock;
        addOperand(elseBlock);
    }

    public void setThenBlock(BasicBlock thenBlock) {
        this.thenBlock = thenBlock;
        operands.set(1, thenBlock);
    }

    public void setElseBlock(BasicBlock elseBlock) {
        this.elseBlock = elseBlock;
        operands.set(2, elseBlock);
    }

    public BasicBlock getThenBlock() {
        return thenBlock;
    }

    public BasicBlock getElseBlock() {
        return elseBlock;
    }

    @Override
    public String toString() {
        return instrType + " i1 " + operands.get(0).getName() + ", label %" +
                operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register reg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        reg = RegisterUtils.loadRegisterValue(operands.get(0), Register.K0, reg);
        new BtypeAsm("bne", operands.get(1).getName(), reg, Register.ZERO);
        new JtypeAsm("j", operands.get(2).getName());
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyCon = functionClone.getValue(this.getOperands().get(0));
        BasicBlock copyThen = (BasicBlock) functionClone.getValue(this.getOperands().get(1));
        BasicBlock copyElse = (BasicBlock) functionClone.getValue(this.getOperands().get(2));
        Instr instr = new BrInstr(copyCon, copyThen, copyElse);
        copyBlock.addInstr(instr);
        return instr;
    }
}
