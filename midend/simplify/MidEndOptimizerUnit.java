package midend.simplify;

import iostream.OptimizerUnit;
import midend.generation.value.construction.Module;
import midend.simplify.method.GlobalVariableNumberingUnit;
import midend.simplify.method.Mem2RegUnit;

public class MidEndOptimizerUnit extends OptimizerUnit {
    private final Module module;
    private final boolean isMem2Reg = true;
    private final boolean isGlobalVariableNumbering = true;

    public MidEndOptimizerUnit(Module module) {
        this.module = module;
    }

    @Override
    public void optimize() {
        if (isMem2Reg) {
            Mem2RegUnit.run(module);
        }
        if (isGlobalVariableNumbering) {
            GlobalVariableNumberingUnit.run(module);
        }
    }

}
