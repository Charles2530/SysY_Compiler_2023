package midend.generation.value.construction;

import backend.mips.asm.textsegment.mipsinstr.JtypeAsm;
import backend.mips.asm.textsegment.structure.Comment;
import backend.mips.asm.textsegment.structure.Label;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import iostream.declare.GetIntDeclare;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;
import midend.simplify.method.BlockSimplifyUnit;

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

    public ArrayList<Function> getFunctions() {
        return functions;
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
            sb.append(function.toString()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        for (GlobalVar globalVar : globalVars) {
            globalVar.generateAssembly();
        }
        for (FormatString stringLiteral : stringLiterals) {
            stringLiteral.generateAssembly();
        }
        new Comment("jump to main function");
        new JtypeAsm("jal", "main");
        new JtypeAsm("j", "end");
        for (Function function : functions) {
            function.generateAssembly();
        }
        new Label("end");
    }

    public void simplifyBlock() {
        for (Function function : this.getFunctions()) {
            function.simplifyBlock();
        }
        for (Function function : this.getFunctions()) {
            BlockSimplifyUnit.deleteDeadBlock(function);
        }
    }
}
