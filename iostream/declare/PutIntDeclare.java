package iostream.declare;

import generation.utils.irtype.VarType;
import generation.value.Value;
import iostream.IoStreamGeneration;

public class PutIntDeclare extends IoStreamGeneration {
    public PutIntDeclare(String name, Value target) {
        super(name, "io", new VarType(0));
        addOperand(target);
    }

    public static String getDeclare() {
        return "declare void @putint(i32)";
    }

}
