package midend.generation.value.construction;

import backend.generation.mips.asm.textsegment.mipsinstr.JtypeAsm;
import backend.generation.mips.asm.textsegment.structure.Comment;
import backend.generation.mips.asm.textsegment.structure.Label;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import iostream.declare.GetIntDeclare;
import iostream.declare.PutIntDeclare;
import iostream.declare.PutStrDeclare;
import midend.simplify.method.BlockSimplifyUnit;

import java.util.ArrayList;

/**
 * Module 是 LLVM IR 中的顶级模块成分，
 * 继承于Value，主要用于生成LLVM IR解析模块
 */
public class Module extends Value {
    /**
     * globalVars 是该 Module 中的全局变量集合
     * functions 是该 Module 中的函数集合
     * stringLiterals 是该 Module 中的字符串常量集合
     */
    private final ArrayList<GlobalVar> globalVars;
    private final ArrayList<Function> functions;
    private final ArrayList<FormatString> stringLiterals;

    public Module() {
        super(new StructType("module"), "module");
        this.globalVars = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.stringLiterals = new ArrayList<>();
    }

    /**
     * addGlobalVar 方法用于向该 Module 中添加全局变量
     */
    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    public ArrayList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    /**
     * addFunction 方法用于向该 Module 中添加函数
     */
    public void addFunction(Function function) {
        functions.add(function);
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    /**
     * addStringLiteral 方法用于向该 Module 中添加字符串常量
     */
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
        new Comment("Jump to main Function");
        new JtypeAsm("jal", "main");
        new JtypeAsm("j", "end");
        for (Function function : functions) {
            function.generateAssembly();
        }
        new Label("end");
    }

    /**
     * simplifyBlock 方法用于简化该 Module 中的所有函数中的基本块，
     * 主要用于删除无用的基本块和死代码删除两部分
     */
    public void simplifyBlock() {
        functions.forEach(Function::simplifyBlock);
        functions.forEach(BlockSimplifyUnit::deleteDeadBlock);
    }
}
