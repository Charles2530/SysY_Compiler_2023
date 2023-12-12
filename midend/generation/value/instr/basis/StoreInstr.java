package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * StoreInstr 用于生成 LLVM IR 中的 store 指令,
 * 继承于 Instr,主要用于写内存
 * store <ty> <value>, <ty>* <pointer>
 */

public class StoreInstr extends Instr {
    public StoreInstr(Value ans, Value res) {
        super(new VarType(0), "StoreInstr", "store");
        addOperand(ans);
        addOperand(res);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getType()
                + " " + operands.get(1).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        // 首先 store 指令的第二个操作数是一个指针，我们需要先读取指针的值
        Register toReg = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        toReg = RegisterUtils.loadPointerValue(operands.get(1), toReg, Register.K1);
        // 首先在寄存器控制器中查找当前指令对应的寄存器，如果为null则使用K0寄存器
        Register fromReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        fromReg = RegisterUtils.loadVariableValue(operands.get(0), fromReg, Register.K0);
        // 然后我们使用 sw 指令将数据写入内存
        new MemTypeAsm("sw", null, fromReg, toReg, 0);
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyAns = functionClone.getValue(this.getOperands().get(0));
        Value copyRes = functionClone.getValue(this.getOperands().get(1));
        Instr instr = new StoreInstr(copyAns, copyRes);
        copyBlock.addInstr(instr);
        return instr;
    }
}
