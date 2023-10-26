package backend.simplify;

import backend.simplify.method.PhiEliminationUnit;
import midend.generation.value.construction.Module;

public class BackEndOptimizerUnit {
    private final Module module;
    private final boolean isRemovePhi = true;

    public BackEndOptimizerUnit(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (isRemovePhi) {
            PhiEliminationUnit.run(module);
        }
    }
}
