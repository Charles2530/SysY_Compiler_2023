package midend.generation.value.construction;

import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;

public class Param extends Value {
    private Function belongingFunc;

    public Param(IrType type, String name) {
        super(type, name);
        IrNameController.addParam(this);
    }

    public void setBelongingFunc(Function belongingFunc) {
        this.belongingFunc = belongingFunc;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
