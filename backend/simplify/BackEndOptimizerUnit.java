package backend.simplify;

import backend.simplify.method.PhiEliminationUnit;
import backend.simplify.method.RemoveContinuousBranchUnit;
import iostream.structure.OptimizerUnit;
import midend.generation.GenerationMain;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

public class BackEndOptimizerUnit extends OptimizerUnit {
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
