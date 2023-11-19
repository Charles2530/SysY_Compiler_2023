package midend.simplify.method;

import midend.generation.value.construction.Module;
import midend.generation.value.construction.User;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;

import java.util.HashMap;
import java.util.HashSet;

/**
 * GlobalVariableLocalizeUnit 是全局变量局部化单元，
 * 主要用于全局变量的局部化
 */
public class GlobalVariableLocalizeUnit {
    /**
     * globalVarUsers 是记录的是全局变量和调用它的函数列表
     */
    private static HashMap<GlobalVar, HashSet<Function>> globalVarUsers;

    /**
     * run 是全局变量局部化的主函数
     */
    public static void run(Module module) {
        GlobalVariableLocalizeUnit.init(module);
        module.getGlobalVars().forEach(GlobalVariableLocalizeUnit::analysisGlobalVarUsers);
        FunctionInlineUnit.buildFuncCallGraph();
        module.getGlobalVars().forEach(GlobalVariableLocalizeUnit::localize);
    }

    /**
     * init 是全局变量局部化的初始化函数
     */
    private static void init(Module module) {
        globalVarUsers = new HashMap<>();
        for (GlobalVar var : module.getGlobalVars()) {
            globalVarUsers.put(var, new HashSet<>());
        }
    }

    /**
     * analysisGlobalVarUsers 是分析全局变量使用者的函数
     */
    private static void analysisGlobalVarUsers(GlobalVar var) {
        for (User user : var.getUsers()) {
            if (user instanceof Instr instr) {
                Function userFunc = instr.getBelongingBlock().getBelongingFunc();
                globalVarUsers.get(var).add(userFunc);
            }
        }
    }

    /**
     * localize 是局部化全局变量的函数
     */
    private static void localize(GlobalVar globalVar) {
    }
}
