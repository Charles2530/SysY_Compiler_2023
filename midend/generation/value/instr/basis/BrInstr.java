package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.mips.asm.textsegment.mipsinstr.BtypeAsm;
import backend.mips.asm.textsegment.mipsinstr.JtypeAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
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

    /*TODO:probabaly error*/
    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register reg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        reg = RegisterUtils.loadRegisterValue(operands.get(0), Register.K0, reg);
        new BtypeAsm("bne", operands.get(1).getName(), reg, Register.ZERO);
        new JtypeAsm("j", operands.get(2).getName());
    }
}
