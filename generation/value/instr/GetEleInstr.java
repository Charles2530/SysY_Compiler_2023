package generation.value.instr;

import generation.utils.IrType;
import generation.utils.irtype.PointerType;
import generation.value.construction.Instr;

public class GetEleInstr extends Instr {
    public GetEleInstr(String name, String instrType) {
        super(name, instrType);
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
