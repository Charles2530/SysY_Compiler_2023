package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.datasegment.mipsinstr.MdTypeAsm;
import backend.mips.asm.datasegment.mipsinstr.MemTypeAsm;
import backend.mips.asm.datasegment.mipsinstr.RtypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class CalcInstr extends Instr {
    public CalcInstr(String name, String instrType, Value op1, Value op2) {
        super(new VarType(32), name, instrType);
        addOperand(op1);
        addOperand(op2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" i32 ")
                .append(operands.get(0).getName()).append(
                        ", ").append(operands.get(1).getName());
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        Register rs = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        RegisterUtils.loadRegVal(operands.get(0), rs, Register.K0);
        Register rt = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        RegisterUtils.loadRegVal(operands.get(1), rt, Register.K1);
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
                new MdTypeAsm("mul", rs, rt);
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
        if (AssemblyUnit.getRegisterController().getRegister(this) == null) {
            AssemblyUnit.moveCurrentOffset(-4);
            AssemblyUnit.addOffset(this, AssemblyUnit.getCurrentOffset());
            new MemTypeAsm("sw", null, target, Register.SP, AssemblyUnit.getCurrentOffset());
        }
    }
}
