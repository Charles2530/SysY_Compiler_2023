package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class IcmpInstr extends Instr {
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
        RegisterUtils.reAllocReg(this, target);
    }

    @Override
    public String getGlobalVariableNumberingHash() {
        return operands.get(0).getName() + " " + instrType + " " + operands.get(1).getName();
    }
}
