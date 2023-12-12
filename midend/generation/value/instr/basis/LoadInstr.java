package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
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
        // 首先 load 指令的第一个操作数是一个指针，我们需要先读取指针的值
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        // 在寄存器控制器中查找当前指令对应的寄存器，如果为null则使用K0寄存器
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        // 然后我们使用 lw 指令从内存中读取数据
        new MemTypeAsm("lw", null, target, pointerReg, 0);
        // 如果使用了默认寄存器，那么我们需要重新申请空间
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyPtr = functionClone.getValue(this.getOperands().get(0));
        Instr instr = new LoadInstr(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline", copyPtr);
        copyBlock.addInstr(instr);
        return instr;
    }
}
