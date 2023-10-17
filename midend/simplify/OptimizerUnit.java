package midend.simplify;

import midend.generation.value.construction.Module;
import midend.simplify.method.BlockSimplifyUnit;

public class OptimizerUnit {
    private Module module;
    private final boolean isBlockSimplyfy = true;

    public OptimizerUnit(Module module) {
        this.module = module;
    }

    public void optimize() {
        if (isBlockSimplyfy) {
            BlockSimplifyUnit.run(module);
        }
    }
}
