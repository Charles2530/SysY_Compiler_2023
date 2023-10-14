package generation.value.construction.user;

import generation.utils.IrType;
import generation.value.construction.User;
import generation.value.construction.procedure.Initial;

public class GlobalVar extends User {
    private Initial initial;

    public GlobalVar(IrType type, String name, Initial initial) {
        super(type, name);
        this.initial = initial;
    }
}
