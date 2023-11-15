package midend.generation.value.construction;

import midend.generation.utils.IrType;
import midend.generation.value.Value;

/**
 * Constant 是 LLVM IR 中的常量成分，
 * 继承于Value，主要用于生成常量
 */
public class Constant extends Value {
    /**
     * val 是该 Constant 的值
     * isDefined 表示该 Constant 是否被定义,如果未被定义则为val会被初始化为0
     */
    private final int val;
    private final boolean isDefined;

    public Constant(String num, IrType type, boolean... isDefined) {
        super(type, num);
        this.val = Integer.parseInt(num);
        if (isDefined.length > 0) {
            this.isDefined = isDefined[0];
        } else {
            this.isDefined = true;
        }
    }

    public boolean isDefined() {
        return isDefined;
    }

    public int getVal() {
        return val;
    }
}
