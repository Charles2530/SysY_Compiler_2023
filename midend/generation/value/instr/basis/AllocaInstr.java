package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.datasegment.mipsinstr.ItypeAsm;
import backend.mips.asm.datasegment.mipsinstr.MemTypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.construction.user.Instr;

public class AllocaInstr extends Instr {
    private final IrType type;

    public AllocaInstr(String name, IrType type) {
        super(new PointerType(type), name, "alloca");
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " = alloca " + type;
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        AssemblyUnit.moveCurrentOffset(
                (type.isArray()) ? -1 * ((ArrayType) type).calcSpaceTot() : -4);
        Register reg = AssemblyUnit.getRegisterController().getRegister(this);
        if (reg != null) {
            new ItypeAsm("addi", reg, Register.SP, AssemblyUnit.getCurrentOffset());
        } else {
            new ItypeAsm("addi", Register.K0, Register.SP, AssemblyUnit.getCurrentOffset());
            RegisterUtils.allocReg(this, Register.K0);
        }
    }
}
