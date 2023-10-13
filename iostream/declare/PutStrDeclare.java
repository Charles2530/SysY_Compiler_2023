package iostream.declare;

import generation.utils.irtype.VarType;
import iostream.IoStreamGeneration;

public class PutStrDeclare extends IoStreamGeneration {
    private String str;

    public PutStrDeclare(String name, String str) {
        super(name, "io", new VarType(0));
        this.str = str;
    }

    public static String getDeclare() {
        return "declare void @putstr(i8* )";
    }

}
