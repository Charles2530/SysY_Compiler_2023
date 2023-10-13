package generation.value.construction;

import generation.utils.IrType;
import generation.value.Value;

public class Constant extends Value {
    private int val;

    public Constant(String num, IrType type) {
        super(type, num);
        this.val = Integer.parseInt(num);
    }

    public int getVal() {
        return val;
    }
}
