package midend.simplify;

import midend.simplify.method.FunctionInlineUnit;
import iostream.structure.OptimizerUnit;
import midend.generation.GenerationMain;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.simplify.method.GlobalVariableNumberingUnit;
import midend.simplify.method.Mem2RegUnit;

public class MidEndOptimizerUnit extends OptimizerUnit {
    private final Module module;
    private final boolean isMem2Reg = true;
    private final boolean isFunctionInline = false;
    private final boolean isGlobalVariableNumbering = true;
    private final boolean isDeadCodeElimination = true;

    public MidEndOptimizerUnit(Module module) {
        this.module = module;
    }

    @Override
    public void optimize() {
        if (isMem2Reg) {
            Mem2RegUnit.run(module);
        }
        if (isFunctionInline) {
            FunctionInlineUnit.run(module);
        }
        if (isGlobalVariableNumbering) {
            GlobalVariableNumberingUnit.run(module);
        }
        if (isDeadCodeElimination) {
            GenerationMain.getModule().getFunctions().forEach(Function::deadCodeElimination);
        }
    }
}
