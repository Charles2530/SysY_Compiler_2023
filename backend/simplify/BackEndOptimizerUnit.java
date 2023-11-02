package backend.simplify;

import backend.simplify.method.PhiEliminationUnit;
import iostream.structure.OptimizerUnit;
import midend.generation.value.construction.Module;

public class BackEndOptimizerUnit extends OptimizerUnit {
    private final Module module;
    private final boolean isRemovePhi = true;

    public BackEndOptimizerUnit(Module module) {
        this.module = module;
    }

    @Override
    public void optimize() {
        if (isRemovePhi) {
            PhiEliminationUnit.run(module);
        }
    }
}
