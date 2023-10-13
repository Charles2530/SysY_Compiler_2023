package iostream.declare;

import iostream.IoStreamGeneration;

public class PutIntDeclare extends IoStreamGeneration {
    public PutIntDeclare(String name, String instrType) {
        super(name, instrType);
    }

    public static String getDeclare() {
        return "declare void @putint(i32)";
    }

}
