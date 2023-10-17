package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.datasegment.mipsinstr.BtypeAsm;
import backend.mips.asm.datasegment.mipsinstr.JtypeAsm;
import backend.mips.asm.datasegment.mipsinstr.MemTypeAsm;
import backend.utils.AssemblyUnit;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;

public class BrInstr extends Instr {
    public BrInstr(Value con, BasicBlock thenBlock, BasicBlock elseBlock) {
        super(new VarType(0), "BrInstr", "br");
        addOperand(con);
        addOperand(thenBlock);
        addOperand(elseBlock);
    }

    @Override
    public String toString() {
        return "br i1 " + operands.get(0).getName() + ", label %" +
                operands.get(1).getName() + ", label %" + operands.get(2).getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register reg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        if (reg == null) {
            reg = Register.K0;
            new MemTypeAsm("lw", null, reg,
                    Register.SP, AssemblyUnit.getOffset(operands.get(0)));
        }
        new BtypeAsm("bne", operands.get(1).getName(), reg, Register.ZERO);
        new JtypeAsm("j", operands.get(2).getName());
    }
}
