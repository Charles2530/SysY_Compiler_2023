package generation.value.instr;

import generation.utils.IrType;
import generation.utils.irtype.PointerType;
import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.user.Instr;

public class GetEleInstr extends Instr {
    public GetEleInstr(String name, String instrType, Value ptr, Value off) {
        super(new PointerType(new VarType(32)), name, instrType);
        addOperand(ptr);
        addOperand(off);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        PointerType ptrType = (PointerType) (operands.get(0).getType());
        IrType type = ptrType.getTarget();
        sb.append(name).append(" = getelementptr inbounds ").append(type).append(" ")
                .append(operands.get(0).getName())
                .append((type.isArray() ? ", i32 " : ", i32 0, i32 "))
                .append(operands.get(1).getName());
        return sb.toString();
    }
}
