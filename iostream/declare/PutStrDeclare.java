package iostream.declare;

import iostream.IoStreamGeneration;

public class PutStrDeclare extends IoStreamGeneration {
    public PutStrDeclare(String name, String instrType) {
        super(name, instrType);
    }

    public static String getDeclare() {
        return "declare void @putstr(i8* )";
    }

}
