package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class RetInstr extends Instr {
    public RetInstr(Value retValue) {
        super(new VarType(0), "RetInstr", "return");
        if (retValue != null) {
            addOperand(retValue);
        }
    }

    @Override
    public String toString() {
        Value retValue = operands.isEmpty() ? null : operands.get(0);
        if (retValue != null) {
            return "ret " + retValue.getType() + " " + retValue.getName();
        }
        return "ret void";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Value retValue = operands.isEmpty() ? null : operands.get(0);
        if (retValue != null) {
            Register retReg = AssemblyUnit.getRegisterController().getRegister(retValue);
            retReg = RegisterUtils.loadVariableValue(retValue, retReg, Register.V0);
            if (retReg != Register.V0) {
                new RtypeAsm("move", Register.V0, retReg, null);
            }
        }
        // jr $ra
        new RtypeAsm("jr", Register.RA);
    }
}
