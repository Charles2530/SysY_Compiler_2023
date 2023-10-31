package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class RetInstr extends Instr {
    public RetInstr(Value retValue) {
        super(new VarType(0), "RetInstr", "ret");
        if (retValue != null) {
            addOperand(retValue);
        }
    }

    @Override
    public String toString() {
        Value retValue = operands.isEmpty() ? null : operands.get(0);
        if (retValue != null) {
            return instrType + " " + retValue.getType() + " " + retValue.getName();
        }
        return instrType + " void";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Value retValue = operands.isEmpty() ? null : operands.get(0);
        if (retValue != null) {
            Register retReg = AssemblyUnit.getRegisterController().getRegister(retValue);
            retReg = RegisterUtils.loadVariableValue(retValue, retReg, Register.V0);
            if (retReg != Register.V0) {
                new MoveAsm(Register.V0, retReg);
            }
        }
        // jr $ra
        new RtypeAsm("jr", Register.RA);
    }
}
