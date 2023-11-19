package midend.simplify.method;

import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Constant;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.User;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.StoreInstr;

import java.util.HashMap;
import java.util.HashSet;

/**
 * GlobalVariableLocalizeUnit 是全局变量局部化单元，
 * 主要用于全局变量的局部化
 */
public class GlobalVariableLocalizeUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     * globalVarUsers 是记录的是全局变量和调用它的函数列表
     */
    private static Module module;
    private static HashMap<GlobalVar, HashSet<Function>> globalVarUsers;

    /**
     * run 是全局变量局部化的主函数
     */
    public static void run(Module module) {
        GlobalVariableLocalizeUnit.module = module;
        GlobalVariableLocalizeUnit.init(module);
        module.getGlobalVars().forEach(GlobalVariableLocalizeUnit::analysisGlobalVarUsers);
        FunctionInlineUnit.buildFuncCallGraph();
        module.getGlobalVars().removeIf(GlobalVariableLocalizeUnit::localize);
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
    private static boolean localize(GlobalVar globalVar) {
        if (globalVarUsers.get(globalVar).isEmpty()) {
            return true;
        }
        if (globalVarUsers.get(globalVar).size() == 1) {
            Function target = globalVarUsers.get(globalVar).iterator().next();
            if (!FunctionInlineUnit.getResponse(target).isEmpty()) {
                return false;
            }
            BasicBlock entry = target.getBasicBlocks().get(0);
            if (globalVar.getType().isInt32()) {
                AllocaInstr allocaInstr = new AllocaInstr(
                        IrNameController.getLocalVarName(target), globalVar.getType());
                entry.insertInstr(0, allocaInstr);
                for (Instr instr : entry.getInstrArrayList()) {
                    if (!(instr instanceof AllocaInstr)) {
                        StoreInstr storeInstr = new StoreInstr(allocaInstr,
                                new Constant(String.valueOf(globalVar.getInitial().getInitValue()),
                                        new VarType(32)));
                        entry.insertInstr(entry.getInstrArrayList().indexOf(instr), storeInstr);
                        break;
                    }
                }
                globalVar.replaceAllUse(allocaInstr);
                return true;
            }
        }
        return false;
    }
}
