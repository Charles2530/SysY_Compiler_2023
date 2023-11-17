package iostream;

import backend.simplify.BackEndOptimizerUnit;
import midend.simplify.MidEndOptimizerUnit;

/**
 * OptimizerUnit 是优化器的基类
 */
public abstract class OptimizerUnit {
    /**
     * initOptimizerSetting 是用于初始化优化器的设置
     * 该方法会将优化器的各个开关设置为默认值
     */
    public static void initOptimizerSetting() {
        OptimizerUnit.isOptimizer = false;
        // 优化全局设置
        Config.setIsGenerationOutput(false);
        Config.setIsOptimize(true);
        Config.setIsGenerationOptimizerOutput(!Config.isIsGenerationOutput());
        Config.setIsAssemblyOutput(true);
        Config.setIsDebugDetailOutput(true);
        // 前端优化设置
        // 中端优化设置
        MidEndOptimizerUnit.setMem2Reg(true);
        MidEndOptimizerUnit.setFunctionInline(true);
        MidEndOptimizerUnit.setGlobalVariableNumbering(true);
        MidEndOptimizerUnit.setDeadCodeElimination(true);
        // 后端优化设置
        BackEndOptimizerUnit.setIsRemovePhi(true);
        BackEndOptimizerUnit.setIsSpaceOptimizer(false);
        BackEndOptimizerUnit.setIsRemoveContinuousBranch(true);
        BackEndOptimizerUnit.setIsRemoveDeadCode(true);
        BackEndOptimizerUnit.setIsBasicBlockSorted(false);
    }

    /**
     * isOptimizer 是用于判断是否开启优化器
     * 若开启则会自动将生成的Function，BasicBlock,
     * Instr,Param,FormatString,GlobalVar添加
     * 到对应的module结构各成分对应父类中
     */
    private static boolean isOptimizer;

    public static boolean isOptimizer() {
        return isOptimizer;
    }

    public static void setIsOptimizer(boolean isOptimizer) {
        OptimizerUnit.isOptimizer = isOptimizer;
    }

    public abstract void optimize();
}
