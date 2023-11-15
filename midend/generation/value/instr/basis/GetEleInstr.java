package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
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
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        Register offsetReg = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        RegisterUtils.loadMemoryOffset(operands.get(1), Register.K1, target, pointerReg, offsetReg);
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        return operands.get(0).getName() + " " + operands.get(1).getName();
    }

    /*TODO:bug maybe*/
    @Override
    public Value copy(FunctionClone functionClone) {
        String suffix = "_" + this.getBelongingBlock().getBelongingFunc().getName().substring(3);
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyPtr = functionClone.getValue(this.getOperands().get(0));
        Value copyOff = functionClone.getValue(this.getOperands().get(1));
        Instr instr = new GetEleInstr(this.getName() + suffix, copyPtr, copyOff);
        copyBlock.addInstr(instr);
        return instr;
    }
}
