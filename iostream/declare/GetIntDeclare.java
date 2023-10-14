package iostream.declare;

import midend.generation.utils.irtype.VarType;
import iostream.IoStreamGeneration;

public class GetIntDeclare extends IoStreamGeneration {
    public GetIntDeclare(String name, String instrType) {
        super(name, instrType, new VarType(32));
    }

    public static String getDeclare() {
        return "declare i32 @getint(...) ";
    }

    @Override
    public String toString() {
        return name + " = call i32 (...) @getint()";
    }
}
