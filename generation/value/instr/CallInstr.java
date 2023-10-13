package generation.value.instr;

import generation.value.construction.Instr;

public class CallInstr extends Instr {
    public CallInstr(String name, String instrType) {
        super(name, instrType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type.isVoid()) {
            sb.append("call void ");
        } else {
            sb.append(name).append(" = call ").append(type).append(" ");
        }
        sb.append(operands.get(0).getName()).append("(");
        for (int i = 1; i < operands.size(); i++) {
            sb.append(operands.get(i).getType()).append(" ").append(operands.get(i).getName());
            if (i != operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
