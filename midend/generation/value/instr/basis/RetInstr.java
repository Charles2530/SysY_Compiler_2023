package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.RtypeAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * RetInstr 用于生成 LLVM IR 中的 ret 指令,
 * 继承于 Instr,主要用于生成函数返回指令
 * ret <type> <value>
 * ret void
 */
public class RetInstr extends Instr {

    public RetInstr(Value retValue) {
        super(new VarType(0), "RetInstr", "ret");
        if (retValue != null) {
            addOperand(retValue);
        }
    }

    public Value getRetValue() {
        return operands.get(0);
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

    @Override
    public Value copy(FunctionClone functionClone) {
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyRetValue = functionClone.getValue(this.getOperands().get(0));
        Instr instr = new RetInstr(copyRetValue);
        copyBlock.addInstr(instr);
        return instr;
    }
}
