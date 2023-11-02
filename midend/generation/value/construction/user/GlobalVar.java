package midend.generation.value.construction.user;

import backend.generation.mips.asm.datasegment.WordAsm;
import iostream.structure.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.construction.User;
import midend.generation.value.construction.procedure.Initial;

public class GlobalVar extends User {
    private final Initial initial;

    public GlobalVar(IrType type, String name, Initial initial) {
        super(type, name);
        this.initial = initial;
        if (!OptimizerUnit.isIsOptimizer()) {
            IrNameController.addGlobalVar(this);
        }
    }

    @Override
    public String toString() {
        return name + " = dso_local global " +
                initial.toString();
    }

    @Override
    public void generateAssembly() {
        IrType target = ((PointerType) type).getTarget();
        if (target.isInt32()) {
            new WordAsm(name.substring(1),
                    String.valueOf(initial.getInitValue().isEmpty() ? 0 :
                            initial.getInitValue().get(0)), null);
        } else {
            new WordAsm(name.substring(1), String.valueOf(((ArrayType) target)
                    .calcSpaceTot()), initial.getInitValue());
        }
    }
}
