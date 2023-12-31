package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * IcmpInstr 用于生成 LLVM IR 中的 icmp 指令,
 * 继承于 Instr,主要用于生成比较指令
 * <result> = icmp <cond> <ty> <op1>, <op2>
 */
public class IcmpInstr extends Instr {
    /**
     * IcmpInstr 主要处理了 LLVM IR 中的比较指令
     * 包含了 eq, ne, sgt, sge, slt, sle
     */
    public IcmpInstr(String name, String instrType, Value ans, Value res) {
        super(new VarType(1), name, instrType);
        addOperand(ans);
        addOperand(res);
    }

    @Override
    public String toString() {
        return name + " = icmp " + instrType + " " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        // 我们需要查找当前指令的操作数对应的寄存器，如果rs为null则使用K0寄存器,
        // 如果rt为null则使用K1寄存器，如果target为null则使用K0寄存器
        Register rs = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        rs = RegisterUtils.loadVariableValue(operands.get(0), rs, Register.K0);
        Register rt = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        rt = RegisterUtils.loadVariableValue(operands.get(1), rt, Register.K1);
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        switch (instrType) {
            case "eq" -> new RtypeAsm("seq", target, rs, rt);
            case "ne" -> new RtypeAsm("sne", target, rs, rt);
            case "sgt" -> new RtypeAsm("sgt", target, rs, rt);
            case "sge" -> new RtypeAsm("sge", target, rs, rt);
            case "slt" -> new RtypeAsm("slt", target, rs, rt);
            case "sle" -> new RtypeAsm("sle", target, rs, rt);
            default -> throw new RuntimeException("Invalid icmp instruction type");
        }
        // 如果使用了默认寄存器，那么我们需要重新申请空间
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        return operands.get(0).getName() + " " + instrType + " " + operands.get(1).getName();
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyAns = functionClone.getValue(this.getOperands().get(0));
        Value copyRes = functionClone.getValue(this.getOperands().get(1));
        Instr instr = new IcmpInstr(
                IrNameController.getLocalVarName(functionClone.getCaller()) + "_Inline",
                this.getInstrType(), copyAns, copyRes);
        copyBlock.addInstr(instr);
        return instr;
    }
}
