package frontend.simplify;

import frontend.simplify.method.FunctionInlineUnit;
import iostream.structure.OptimizerUnit;
import midend.generation.value.construction.Module;

public class FrontEndOptimizerUnit extends OptimizerUnit {
    private final Module module;
    private final boolean isFunctionInline = true;

    public FrontEndOptimizerUnit(Module module) {
        this.module = module;
    }

    @Override
    public void optimize() {
        if (isFunctionInline) {
            FunctionInlineUnit.run(module);
        }
    }
}
