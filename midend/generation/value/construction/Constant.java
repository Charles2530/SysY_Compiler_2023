package midend.generation.value.construction;

import midend.generation.utils.IrType;
import midend.generation.value.Value;

public class Constant extends Value {
    private final int val;

    public Constant(String num, IrType type) {
        super(type, num);
        this.val = Integer.parseInt(num);
    }

    public int getVal() {
        return val;
    }
}
