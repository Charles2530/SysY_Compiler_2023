package midend.generation.value.instr.basis;

import backend.generation.mips.Register;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

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
}
