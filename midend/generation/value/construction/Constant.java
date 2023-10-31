package midend.generation.value.construction;

import midend.generation.utils.IrType;
import midend.generation.value.Value;

public class Constant extends Value {
    private final int val;
    private boolean isDefined;

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
