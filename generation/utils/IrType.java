package generation.utils;

import generation.utils.irtype.ArrayType;
import generation.utils.irtype.VarType;

public class IrType {
    public boolean isVoid() {
        if (this instanceof VarType) {
            return ((VarType) this).getBits() == 0;
        }
        return false;
    }

    public boolean isInt1() {
        if (this instanceof VarType) {
            return ((VarType) this).getBits() == 1;
        }
        return false;
    }

    public boolean isInt8() {
        if (this instanceof VarType) {
            return ((VarType) this).getBits() == 8;
        }
        return false;
    }

    public boolean isArray() {
        return this instanceof ArrayType;
    }
}
