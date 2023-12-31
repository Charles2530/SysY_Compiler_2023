package midend.generation.value.instr.optimizer;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

/**
 * MoveInstr 用于生成 LLVM IR 中的 move 指令,
 * 继承于 Instr,主要用于消除PhiInstr中使用
 */
public class MoveInstr extends Instr {
    /**
     * from 是 move 指令的源操作数
     * to 是 move 指令的目的操作数
     */
    private Value from;
    private Value to;

    public MoveInstr(String name, Value from, Value to) {
        super(new VarType(0), name, "move");
        this.to = to;
        addOperand(to);
        this.from = from;
        addOperand(from);
    }

    public void setFrom(Value from) {
        this.from = from;
        operands.set(1, from);
    }

    public void setTo(Value to) {
        this.to = to;
        operands.set(0, to);
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
        // 首先我们需要将源操作数的值加载到寄存器中
        Register dstReg = AssemblyUnit.getRegisterController().getRegister(to);
        Register srcReg = AssemblyUnit.getRegisterController().getRegister(from);
        // 如果源操作数和目的操作数都是寄存器，我们需要判断是否相同，如果相同则不需要move
        if (dstReg != null && dstReg.equals(srcReg)) {
            return;
        }
        // 如果dstReg为null则使用K0寄存器，如果srcReg为null则使用dstReg寄存器
        dstReg = (dstReg == null) ? Register.K0 : dstReg;
        srcReg = RegisterUtils.loadVariableValue(from, srcReg, dstReg);
        // 如果源操作数和目的操作数都是寄存器，我们需要再次判断是否相同，如果不相同则需要move
        if (!srcReg.equals(dstReg)) {
            new MoveAsm(dstReg, srcReg);
        }
        // 如果使用了默认寄存器，那么我们需要重新申请空间
        RegisterUtils.memAllocReg(to, dstReg);
    }
}
