package midend.generation.value.construction;

import iostream.structure.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

/**
 * Param 是LLVM IR中函数的参数成分，
 * 继承于Value，主要用于生成函数参数
 */
public class Param extends Value {
    /**
     * belongingFunc 是该 Param 所属的函数
     */
    private Function belongingFunc;

    public Param(IrType type, String name) {
        super(type, name);
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addParam(this);
        }
    }

    public void setBelongingFunc(Function belongingFunc) {
        this.belongingFunc = belongingFunc;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
