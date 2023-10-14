package generation.value.construction;

import generation.utils.irtype.StructType;
import generation.value.Value;
import generation.value.construction.user.Function;
import generation.value.construction.user.GlobalVar;
import iostream.declare.GetIntDeclare;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;

import java.util.ArrayList;

public class Module extends Value {
    private final ArrayList<GlobalVar> globalVars;
    private final ArrayList<Function> functions;
    private final ArrayList<FormatString> stringLiterals;

    public Module() {
        super(new StructType("module"), "module");
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

    public void addStringLiteral(FormatString stringLiteral) {
        stringLiterals.add(stringLiteral);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GetIntDeclare.getDeclare()).append("\n");
        sb.append(PutIntDeclare.getDeclare()).append("\n");
        sb.append(PutStrDeclare.getDeclare()).append("\n\n");
        for (FormatString stringLiteral : stringLiterals) {
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
