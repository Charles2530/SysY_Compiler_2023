package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

/**
 * PointerType 是 LLVM IR 中的指针类型，
 * 主要用于生成指针类型
 * 继承自IrType
 */
public class PointerType extends IrType {
    /**
     * target 是该 PointerType 的指向类型
     */
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
