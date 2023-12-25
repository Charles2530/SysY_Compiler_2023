package iostream;

import backend.simplify.BackEndOptimizerUnit;
import midend.generation.value.construction.Module;
import midend.simplify.MidEndOptimizerUnit;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;

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
        Config.setIsGenerationOutput(true);
        Config.setIsOptimize(false);
        Config.setIsGenerationOptimizerOutput(!Config.isIsGenerationOutput());
        Config.setIsAssemblyOutput(true);
        Config.setIsDebugDetailOutput(true);
        // 前端优化设置
        // 中端优化设置
        MidEndOptimizerUnit.setGlobalVarLocalize(true);
        MidEndOptimizerUnit.setMem2Reg(true);
        MidEndOptimizerUnit.setFunctionInline(true);
        MidEndOptimizerUnit.setGlobalVariableNumbering(true);
        MidEndOptimizerUnit.setIsGlobalCodeMovement(true);
        MidEndOptimizerUnit.setDeadCodeElimination(true);
        // 后端优化设置
        BackEndOptimizerUnit.setIsRemovePhi(true);
        BackEndOptimizerUnit.setIsSpaceOptimizer(false);
        BackEndOptimizerUnit.setIsRemoveContinuousBranch(true);
        BackEndOptimizerUnit.setIsRemoveDeadCode(true);
        BackEndOptimizerUnit.setIsBasicBlockSorted(false);
    }

    /**
     * build() 是重构了优化时期的数据结构，
     * 这里构建了CFG图，支配树和活跃性分析
     *
     * @param module 是LLVM IR生成的顶级模块
     */
    public static void build(Module module) {
        ControlFlowGraph.build(module);
        DominatorTree.build(module);
        LivenessAnalysisController.analysis(module);
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
