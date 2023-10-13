package generation.utils.irtype;

import generation.utils.IrType;

public class ArrayType extends IrType {
    private int eleNum;
    private IrType eleType;

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
