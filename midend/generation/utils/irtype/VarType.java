package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

public class VarType extends IrType {
    private final int bits;

    public VarType(int bits) {
        this.bits = bits;
    }

    public int getBits() {
        return bits;
    }

    @Override
    public String toString() {
        return switch (bits) {
            case 1 -> "i1";
            case 8 -> "i8";
            case 32 -> "i32";
            default -> "void";
        };
    }

}
