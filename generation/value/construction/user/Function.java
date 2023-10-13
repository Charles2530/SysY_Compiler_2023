package generation.value.construction.user;

import generation.utils.IrType;
import generation.utils.irtype.StructType;
import generation.value.construction.User;

public class Function extends User {
    private IrType returnType;

    public Function(String name, IrType returnType) {
        super(new StructType("function"), name);
        this.returnType = returnType;
    }
}
