package iostream.declare;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.FormatString;
import iostream.IoStreamGeneration;

public class PutStrDeclare extends IoStreamGeneration {
    private final FormatString str;

    public PutStrDeclare(String name, FormatString str) {
        super(name, "io", new VarType(0));
        this.str = str;
    }

    public static String getDeclare() {
        return "declare void @putch(i32)\ndeclare void @putstr(i8*)";
    }

    @Override
    public String toString() {
        return "call void @putstr(i8* getelementptr inbounds (" +
                str.getPointer().getTarget() + ", " +
                str.getPointer() + " " +
                str.getName() + ", i64 0, i64 0))";
    }
}
