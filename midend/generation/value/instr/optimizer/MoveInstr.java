package midend.generation.value.instr.optimizer;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class MoveInstr extends Instr {
    private Value from;
    private Value to;

    public MoveInstr(String name, Value from, Value to) {
        super(new VarType(0), name, "move");
        this.from = from;
        addOperand(from);
        this.to = to;
        addOperand(to);
    }

    public void setFrom(Value from) {
        this.from = from;
    }

    public void setTo(Value to) {
        this.to = to;
    }

    public Value getFrom() {
        return from;
    }

    public Value getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "move " + to.getType() + " " +
                to.getName() + ", " + from.getName();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register dstReg = AssemblyUnit.getRegisterController().getRegister(to);
        Register srcReg = AssemblyUnit.getRegisterController().getRegister(from);
        if (dstReg != null && dstReg.equals(srcReg)) {
            return;
        }
        dstReg = (dstReg == null) ? Register.K0 : dstReg;
        srcReg = RegisterUtils.loadVariableValue(from, srcReg, dstReg);
        if (!srcReg.equals(dstReg)) {
            new MoveAsm(dstReg, srcReg);
        }
        RegisterUtils.memAllocReg(to, dstReg);
    }
}
