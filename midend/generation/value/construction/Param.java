package midend.generation.value.construction;

import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

public class Param extends Value {
    private Function belongingFunc;

    public Param(IrType type, String name) {
        super(type, name);
        if (!OptimizerUnit.isIsOptimizer()) {
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
