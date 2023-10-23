package midend.simplify;

import midend.generation.value.construction.Module;
import midend.simplify.method.BlockSimplifyUnit;

public class OptimizerUnit {
    private final Module module;
    private final boolean isBlockSimplify = true;

    public OptimizerUnit(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (isBlockSimplify) {
            BlockSimplifyUnit.run(module);
        }
    }
}
