package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

/**
 * StructType 是 LLVM IR 中的结构类型，
 * 主要用于生成结构类型，如Function，BasicBlock等
 * 继承自IrType
 */
public class StructType extends IrType {
    /**
     * name 是该 StructType 的名称
     */
    private final String name;

    public StructType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "i32";
    }
}
