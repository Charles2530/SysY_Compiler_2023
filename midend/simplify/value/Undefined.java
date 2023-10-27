package midend.simplify.value;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;

public class Undefined extends Value {

    public Undefined() {
        super(new VarType(32), "0");
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
