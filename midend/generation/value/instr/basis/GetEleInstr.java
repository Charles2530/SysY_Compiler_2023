package midend.generation.value.instr.basis;

import backend.mips.Register;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Instr;

public class GetEleInstr extends Instr {
    public GetEleInstr(String name, Value ptr, Value off) {
        super(new PointerType(new VarType(32)), name, "getEle");
        addOperand(ptr);
        addOperand(off);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        PointerType ptrType = (PointerType) (operands.get(0).getType());
        IrType type = ptrType.getTarget();
        sb.append(name).append(" = getelementptr inbounds ")
                .append(type).append(", ")
                .append(ptrType).append(" ")
                .append(operands.get(0).getName())
                .append((type.isArray() ? ", i32 " : ", i32 0, i32 "))
                .append(operands.get(1).getName());
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register target = AssemblyUnit.getRegisterController().getRegister(this);
        target = (target == null) ? Register.K0 : target;
        Register pointerReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        pointerReg = RegisterUtils.loadPointerValue(operands.get(0), pointerReg, Register.K0);
        Register offsetReg = AssemblyUnit.getRegisterController().getRegister(operands.get(1));
        RegisterUtils.loadMemoryOffset(operands.get(1), Register.K1, target, pointerReg, offsetReg);
        RegisterUtils.reAllocReg(this, target);
    }
}
