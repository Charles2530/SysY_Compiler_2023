package midend.generation.utils;

import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.VarType;

/**
 * IrType 是 LLVM IR 中的类型成分，
 * 主要用于生成类型，IrType是各种子类型的公共父类
 */
public class IrType {
    /**
     * 为了方便生成代码，快速判断IrType的类型，我们在IrType中定义了一些方法
     * isVoid() 判断该类型是否为void
     * isInt1() 判断该类型是否为i1
     * isInt32() 判断该类型是否为i32
     * isArray() 判断该类型是否为数组
     * isPointer() 判断该类型是否为指针
     */
    public boolean isVoid() {
        return this instanceof VarType && ((VarType) this).getBits() == 0;
    }

    public boolean isInt1() {
        return this instanceof VarType && ((VarType) this).getBits() == 1;
    }

    public boolean isInt32() {
        return this instanceof VarType && ((VarType) this).getBits() == 32;
    }

    public boolean isArray() {
        return this instanceof ArrayType;
    }

    public boolean isPointer() {
        return this instanceof PointerType;
    }
}
