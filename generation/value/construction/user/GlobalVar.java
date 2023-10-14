package generation.value.construction.user;

import generation.utils.IrNameController;
import generation.utils.IrType;
import generation.value.construction.User;
import generation.value.construction.procedure.Initial;

public class GlobalVar extends User {
    private Initial initial;

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
