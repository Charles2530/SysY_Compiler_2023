package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.MdTypeAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.user.Instr;

public class CalcInstr extends Instr {
    public CalcInstr(String name, String instrType, Value op1, Value op2) {
        super(new VarType(32), name, instrType);
        addOperand(op1);
        addOperand(op2);
    }

    public Integer getConstantNum() {
        return ((operands.get(0) instanceof Constant) ? 1 : 0) +
                ((operands.get(1) instanceof Constant) ? 1 : 0);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " i32 " +
                operands.get(0).getName() +
                ", " + operands.get(1).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        Register rs = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        rs = RegisterUtils.loadVariableValue(operands.get(0), rs, Register.K0);
        Register rt = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        rt = RegisterUtils.loadVariableValue(operands.get(1), rt, Register.K1);
        switch (instrType) {
            case "add":
                new RtypeAsm("addu", target, rs, rt);
                break;
            case "sub":
                new RtypeAsm("subu", target, rs, rt);
                break;
            case "and":
                new RtypeAsm("and", target, rs, rt);
                break;
            case "or":
                new RtypeAsm("or", target, rs, rt);
                break;
            case "mul":
                new MdTypeAsm("mult", rs, rt);
                new MdTypeAsm("mflo", target);
                break;
            case "sdiv":
                new MdTypeAsm("div", rs, rt);
                new MdTypeAsm("mflo", target);
                break;
            case "srem":
                new MdTypeAsm("div", rs, rt);
                new MdTypeAsm("mfhi", target);
                break;
            default:
                throw new RuntimeException("Unknown instruction type: " + instrType);
        }
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        if (instrType.matches("add|mul") &&
                operands.get(0).getName().compareTo(operands.get(1).getName()) > 0) {
            return operands.get(1).getName() + " " + instrType + " " + operands.get(0).getName();
        }
        return operands.get(0).getName() + " " + instrType + " " + operands.get(1).getName();
    }
}
