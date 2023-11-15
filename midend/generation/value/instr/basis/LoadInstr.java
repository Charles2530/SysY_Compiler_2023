package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * LoadInstr 用于生成 LLVM IR 中的 load 指令,
 * 继承于 Instr,主要用于读取内存
 * <result> = load <ty>, <ty>* <pointer>
 */
public class LoadInstr extends Instr {
    public LoadInstr(String name, Value ptr) {
        super(((PointerType) ptr.getType()).getTarget(), name, "load");
        addOperand(ptr);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + type + ", " +
                operands.get(0).getType() + " " + operands.get(0).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        new MemTypeAsm("lw", null, target, pointerReg, 0);
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyPtr = functionClone.getValue(this.getOperands().get(0));
        Instr instr = new LoadInstr(this.getName(), copyPtr);
        copyBlock.addInstr(instr);
        return instr;
    }
}
