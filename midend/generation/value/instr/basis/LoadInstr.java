package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class LoadInstr extends Instr {
    public LoadInstr(String name, Value ptr) {
        super(((PointerType) ptr.getType()).getTarget(), name, "load");
        addOperand(ptr);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + type + ", " +
                operands.get(0).getType() + " " + operands.get(0).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        new MemTypeAsm("lw", null, target, pointerReg, 0);
        RegisterUtils.reAllocReg(this, target);
    }
}
