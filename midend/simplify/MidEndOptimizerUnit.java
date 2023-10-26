package midend.simplify;

import midend.generation.value.construction.Module;
import midend.simplify.method.BlockSimplifyUnit;
import midend.simplify.method.DeadCodeEliminationUnit;
import midend.simplify.method.Mem2RegUnit;

public class MidEndOptimizerUnit {
    private final Module module;
    private final boolean isBlockSimplify = true;
    private final boolean isMem2Reg = true;
    private final boolean isDeadCodeElimination = true;

    public MidEndOptimizerUnit(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (isBlockSimplify) {
            BlockSimplifyUnit.run(module);
        }
        if (isMem2Reg) {
            Mem2RegUnit.run(module);
        }
        if (isDeadCodeElimination) {
            DeadCodeEliminationUnit.run(module);
        }
    }

}
