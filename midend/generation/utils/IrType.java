package midend.generation.utils;

import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.VarType;

public class IrType {
    public boolean isVoid() {
        return this instanceof VarType && ((VarType) this).getBits() == 0;
    }

    public boolean isInt1() {
        return this instanceof VarType && ((VarType) this).getBits() == 1;
    }

    public boolean isInt8() {
        return this instanceof VarType && ((VarType) this).getBits() == 8;
    }

    public boolean isInt32() {
        return this instanceof VarType && ((VarType) this).getBits() == 32;
    }

    public boolean isArray() {
        return this instanceof ArrayType;
    }
}
