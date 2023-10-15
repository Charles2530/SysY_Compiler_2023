package midend.generation.utils.irtype;

import midend.generation.utils.IrType;

public class StructType extends IrType {
    private final String name;

    public StructType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "i32";
    }
}
