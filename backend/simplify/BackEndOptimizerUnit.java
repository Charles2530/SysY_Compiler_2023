package backend.simplify;

import backend.simplify.method.PhiEliminationUnit;
import backend.simplify.method.RemoveContinuousBranchUnit;
import iostream.structure.OptimizerUnit;
import midend.generation.GenerationMain;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

/**
 * BackEndOptimizerUnit 是后端优化器单元，
 * 主要用于MIPS后端优化
 * 继承自OptimizerUnit
 */
public class BackEndOptimizerUnit extends OptimizerUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     * isRemovePhi 是是否移除phi指令
     * isSpaceOptimizer 是是否进行Space宏优化(写完后发现似乎不开效率更高)
     * isRemoveContinuousBranch 是是否移除连续分支
     * isRemoveDeadCode 是是否移除死代码
     * isBasicBlockSorted 是是否对基本块进行排序优化
     */
    private final Module module;
    private final boolean isRemovePhi = true;
    private static boolean isSpaceOptimizer = false;
    private static boolean isRemoveContinuousBranch = true;
    private static boolean isRemoveDeadCode = true;
    private static boolean isBasicBlockSorted = false;

    public BackEndOptimizerUnit(Module module) {
        this.module = module;
    }

    public static boolean isSpaceOptimizer() {
        return isSpaceOptimizer;
    }

    public static boolean isBasicBlockSorted() {
        return isBasicBlockSorted;
    }

    @Override
    public void optimize() {
        if (isRemovePhi) {
            PhiEliminationUnit.run(module);
        }
        if (isRemoveContinuousBranch) {
            RemoveContinuousBranchUnit.run(module);
        }
        if (isRemoveDeadCode) {
            GenerationMain.getModule().getFunctions().forEach(Function::deadCodeElimination);
        }
    }
}
