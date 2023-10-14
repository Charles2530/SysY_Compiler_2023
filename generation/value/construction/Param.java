package generation.value.construction;

import generation.utils.IrNameController;
import generation.utils.IrType;
import generation.value.Value;
import generation.value.construction.user.Function;

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
