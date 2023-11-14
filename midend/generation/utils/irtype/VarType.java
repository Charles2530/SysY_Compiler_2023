package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

/**
 * VarType 是 LLVM IR 中的变量类型，
 * 主要用于生成变量类型
 * 继承自IrType
 */
public class VarType extends IrType {
    /**
     * bits 是该 VarType 的位数信息
     * 0 表示 void
     * 1 表示 i1
     * 32 表示 i32
     */
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
            case 32 -> "i32";
            default -> "void";
        };
    }
}
