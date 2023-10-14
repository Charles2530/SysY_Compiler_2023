package generation.utils.irtype;

import generation.utils.IrType;

public class PointerType extends IrType {
    private IrType target;

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
