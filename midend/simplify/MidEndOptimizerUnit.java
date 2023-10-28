package midend.simplify;

import iostream.OptimizerUnit;
import midend.generation.value.construction.Module;
import midend.simplify.method.BlockSimplifyUnit;
import midend.simplify.method.Mem2RegUnit;

public class MidEndOptimizerUnit extends OptimizerUnit {
    private final Module module;
    private final boolean isBlockSimplify = true;
    private final boolean isMem2Reg = true;

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
    }

}
