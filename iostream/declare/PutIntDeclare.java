package iostream.declare;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import iostream.IoStreamGeneration;

public class PutIntDeclare extends IoStreamGeneration {
    public PutIntDeclare(Value target) {
        super("PutIntDeclare", "io", new VarType(0));
        addOperand(target);
    }

    public static String getDeclare() {
        return "declare void @putint(i32)";
    }

    @Override
    public String toString() {
        return "call void @putint(i32 " + operands.get(0).getName() + ")";
    }
}
