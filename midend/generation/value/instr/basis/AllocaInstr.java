package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.mipsinstr.ItypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

public class AllocaInstr extends Instr {
    private final IrType type;

    public AllocaInstr(String name, IrType type) {
        super(new PointerType(type), name, "alloca");
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + type;
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        AssemblyUnit.moveCurrentOffset(
                (type.isArray()) ? (-4 * ((ArrayType) type).calcSpaceTot()) : -4);
        Register reg = AssemblyUnit.getRegisterController().getRegister(this);
        if (reg != null) {
            new ItypeAsm("addi", reg, Register.SP, AssemblyUnit.getCurrentOffset());
        } else {
            new ItypeAsm("addi", Register.K0, Register.SP, AssemblyUnit.getCurrentOffset());
            RegisterUtils.allocReg(this, Register.K0);
        }
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Instr instr = new AllocaInstr(this.getName() + "_copy", this.type);
        copyBlock.addInstr(instr);
        return instr;
    }
}
