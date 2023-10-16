package midend.simplify;

import midend.generation.value.construction.Module;
import midend.simplify.method.BlockSimplifyUnit;

public class OptimizerUnit {
    private Module module;
    private final boolean isBlockSimplyfy = true;
    private BlockSimplifyUnit blockSimplifyUnit;

    public OptimizerUnit(Module module) {
        this.module = module;
        if (isBlockSimplyfy) {
            blockSimplifyUnit = new BlockSimplifyUnit();
        }
    }

    public void optimize() {
        if (isBlockSimplyfy) {
            blockSimplifyUnit.run(module);
        }
    }
}
