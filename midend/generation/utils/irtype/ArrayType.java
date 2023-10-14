package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

public class ArrayType extends IrType {
    private final int eleNum;
    private final IrType eleType;

    public ArrayType(int eleNum, IrType eleType) {
        this.eleNum = eleNum;
        this.eleType = eleType;
    }

    public IrType getEleType() {
        return eleType;
    }

    public int getEleNum() {
        return eleNum;
    }

    @Override
    public String toString() {
        return "[" + eleNum + " x " + eleType + "]";
    }
}
