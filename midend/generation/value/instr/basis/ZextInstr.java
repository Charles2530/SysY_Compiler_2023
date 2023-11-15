package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.FunctionClone;

/**
 * ZextInstr 用于生成 LLVM IR 中的 zext 指令,
 * 继承于 Instr,主要用于生成零扩展指令
 * <result> = zext <ty> <value> to <ty2>
 */
public class ZextInstr extends Instr {
    private final IrType target;

    public ZextInstr(String name, String instrType, Value val, IrType target) {
        super(target, name, instrType);
        this.target = target;
        addOperand(val);
    }

    @Override
    public String toString() {
        return name + " = zext " + operands.get(0).getType() + " " +
                operands.get(0).getName() + " to " + target;
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        if (operands.get(0).getType().isInt1() && target.isInt32()) {
            Register oriReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
            if (oriReg != null) {
                RegisterUtils.allocReg(this, oriReg);
            } else {
                AssemblyUnit.addOffset(this, AssemblyUnit.getOffset(operands.get(0)));
            }
        }
    }

    @Override
    public Value copy(FunctionClone functionClone) {
        String suffix = "_" + this.getBelongingBlock().getBelongingFunc().getName().substring(3);
        BasicBlock copyBlock = (BasicBlock) functionClone.getValue(this.getBelongingBlock());
        Value copyVal = functionClone.getValue(this.getOperands().get(0));
        Instr instr = new ZextInstr(this.getName() + suffix,
                this.getInstrType(), copyVal, this.target);
        copyBlock.addInstr(instr);
        return instr;
    }
}
