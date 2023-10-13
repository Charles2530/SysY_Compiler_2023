package generation.value;

import generation.utils.IrType;

public class Value {
    protected IrType type;

    protected String name;

    public Value(IrType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IrType getType() {
        return type;
    }
}
