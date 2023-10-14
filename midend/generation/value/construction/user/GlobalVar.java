package midend.generation.value.construction.user;

import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.construction.User;
import midend.generation.value.construction.procedure.Initial;

public class GlobalVar extends User {
    private final Initial initial;

    public GlobalVar(IrType type, String name, Initial initial) {
        super(type, name);
        this.initial = initial;
        IrNameController.addGlobalVar(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = dso_local global ");
        sb.append(initial.toString());
        return sb.toString();
    }
}
