package iostream.declare;

import iostream.IoStreamGeneration;

public class GetIntDeclare extends IoStreamGeneration {
    public GetIntDeclare(String name, String instrType) {
        super(name, instrType);
    }

    public static String getDeclare() {
        return "declare i32 @getint(...) ";
    }
}
