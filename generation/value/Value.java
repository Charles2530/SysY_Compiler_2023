package generation.value;

import generation.utils.IrType;

public class Value {
    protected IrType type;

    protected String name;

    public Value(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IrType getType() {
        return type;
    }
}
