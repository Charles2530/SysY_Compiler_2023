package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

public class PointerType extends IrType {
    private final IrType target;

    public PointerType(IrType target) {
        this.target = target;
    }

    public IrType getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return target.toString() + "*";
    }
}
