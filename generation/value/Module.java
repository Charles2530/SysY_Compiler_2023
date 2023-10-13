package generation.value;

import generation.value.construction.Function;
import generation.value.construction.GlobalVar;
import iostream.declare.GetIntDeclare;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;

import java.util.ArrayList;

public class Module extends Value {
    private final ArrayList<GlobalVar> globalVars;
    private final ArrayList<Function> functions;
    private final ArrayList<String> stringLiterals;

    public Module() {
        super("module");
        this.globalVars = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.stringLiterals = new ArrayList<>();
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addStringLiteral(String stringLiteral) {
        stringLiterals.add(stringLiteral);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetIntDeclare.getDeclare()).append("\n");
        sb.append(PutIntDeclare.getDeclare()).append("\n");
        sb.append(PutStrDeclare.getDeclare()).append("\n\n");
        for (String stringLiteral : stringLiterals) {
            sb.append(stringLiteral).append("\n");
        }
        sb.append("\n");
        for (GlobalVar globalVar : globalVars) {
            sb.append(globalVar.toString()).append("\n");
        }
        sb.append("\n");
        for (Function function : functions) {
            sb.append(function.toString()).append("\n");
        }
        return sb.toString();
    }
}
