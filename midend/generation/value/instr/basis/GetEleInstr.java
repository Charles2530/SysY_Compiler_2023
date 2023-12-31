package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

import java.util.ArrayList;

/**
 * GetEleInstr 用于生成 LLVM IR 中的 getelementptr 指令,
 * 继承于 Instr,主要用于生成数组元素访问指令
 * <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
 * <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
 */
public class GetEleInstr extends Instr {
    public GetEleInstr(String name, Value ptr, Value off) {
        super(new PointerType(new VarType(32)), name, "getEle");
        addOperand(ptr);
        addOperand(off);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        PointerType ptrType = (PointerType) (operands.get(0).getType());
        IrType type = ptrType.getTarget();
        sb.append(name).append(" = getelementptr inbounds ")
                .append(type).append(", ")
                .append(ptrType).append(" ")
                .append(operands.get(0).getName())
                .append((type.isArray() ? ", i32 0, i32 " : ", i32 "));
        if (type.isArray() && ((ArrayType) type).getEleSpace().size() == 2) {
            if (operands.get(1).getName().matches("[0-9]+")) {
                ArrayList<Integer> spaces = ((ArrayType) type).getEleSpace();
                Integer offset = Integer.parseInt(operands.get(1).getName());
                sb.append(offset / spaces.get(1)).append(", i32 ").append(offset % spaces.get(1));
            } else {
                sb.append("0, i32 ").append(operands.get(1).getName());
            }
        } else {
            sb.append(operands.get(1).getName());
        }
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        // 首先在寄存器控制器中查找当前指令对应的寄存器，如果为null则使用K0寄存器
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        // 同理，我们也需要查找当前指令的操作数对应的寄存器，如果rs为null则使用K0寄存器,
        // 如果rt为null则使用K1寄存器,当然这里由于我们的操作数是一个指针，所以我们还需要
        // 从指针中获取对应的地址
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        Register offsetReg = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        RegisterUtils.loadMemoryOffset(operands.get(1), Register.K1, target, pointerReg, offsetReg);
        // 如果之前使用了默认寄存器，则需要重新分配地址，并在寄存器控制器中更新对应的寄存器
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        return operands.get(0).getName() + " " + operands.get(1).getName();
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyPtr = functionClone.getValue(this.getOperands().get(0));
        Value copyOff = functionClone.getValue(this.getOperands().get(1));
        Instr instr = new GetEleInstr(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline",
                copyPtr, copyOff);
        copyBlock.addInstr(instr);
        return instr;
    }
}
