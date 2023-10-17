package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.datasegment.mipsinstr.RtypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class IcmpInstr extends Instr {
    public IcmpInstr(String name, String instrType, Value ans, Value res) {
        super(new VarType(1), name, instrType);
        operands.add(ans);
        operands.add(res);
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
        new RtypeAsm(instrType, target, rs, rt);
        RegisterUtils.reAllocReg(this, target);
    }
}
