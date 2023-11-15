package midend.simplify;

import midend.simplify.method.FunctionInlineUnit;
import iostream.OptimizerUnit;
import midend.generation.GenerationMain;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.simplify.method.GlobalVariableNumberingUnit;
import midend.simplify.method.Mem2RegUnit;

/**
 * MidEndOptimizerUnit 是中间优化单元，
 * 主要用于中间代码的优化
 * 继承自OptimizerUnit
 */
public class MidEndOptimizerUnit extends OptimizerUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     * isMem2Reg 是该 MidEndOptimizerUnit 的Mem2Reg优化开关
     * isFunctionInline 是该 MidEndOptimizerUnit 的函数内联优化开关
     * isGlobalVariableNumbering 是该 MidEndOptimizerUnit 的全局变量编号优化开关
     * isDeadCodeElimination 是该 MidEndOptimizerUnit 的死代码消除优化开关
     */
    private final Module module;
    private static boolean isMem2Reg;
    private static boolean isFunctionInline;
    private static boolean isGlobalVariableNumbering;
    private static boolean isDeadCodeElimination;

    public MidEndOptimizerUnit(Module module) {
        this.module = module;
    }

    @Override
    public void optimize() {
        if (isMem2Reg) {
            Mem2RegUnit.run(module);
        }
        if (isFunctionInline) {
            FunctionInlineUnit.run(module);
        }
        if (isGlobalVariableNumbering) {
            GlobalVariableNumberingUnit.run(module);
        }
        if (isDeadCodeElimination) {
            GenerationMain.getModule().getFunctions().forEach(Function::deadCodeElimination);
        }
    }

    public static void setMem2Reg(boolean mem2Reg) {
        isMem2Reg = mem2Reg;
    }

    public static void setFunctionInline(boolean functionInline) {
        isFunctionInline = functionInline;
    }

    public static void setGlobalVariableNumbering(boolean globalVariableNumbering) {
        isGlobalVariableNumbering = globalVariableNumbering;
    }

    public static void setDeadCodeElimination(boolean deadCodeElimination) {
        isDeadCodeElimination = deadCodeElimination;
    }
}
