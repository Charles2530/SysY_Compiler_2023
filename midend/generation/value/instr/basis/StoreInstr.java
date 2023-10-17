package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class StoreInstr extends Instr {
    public StoreInstr(Value ans, Value res) {
        super(new VarType(0), "StoreInstr", "store");
        addOperand(ans);
        addOperand(res);
    }

    @Override
    public String toString() {
        return "store " + operands.get(0).getType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getType()
                + " " + operands.get(1).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register fromReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        fromReg = RegisterUtils.loadVariableValue(operands.get(0), fromReg, Register.K0);
        Register toReg = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        toReg = RegisterUtils.loadPointerValue(operands.get(1), toReg, Register.K1);
        new MemTypeAsm("sw", null, fromReg, toReg, 0);
    }
}
