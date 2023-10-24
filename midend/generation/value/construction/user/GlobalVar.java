package midend.generation.value.construction.user;

import backend.mips.Register;
import backend.mips.asm.textsegment.complex.LiAsm;
import backend.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.mips.asm.datasegment.SpaceAsm;
import backend.mips.asm.datasegment.WordAsm;
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
        IrNameController.addGlobalVar(this);
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
                            initial.getInitValue().get(0)));
        } else {
            new SpaceAsm(name.substring(1),
                    String.valueOf(((ArrayType) target).calcSpaceTot() * 4));
            if (!initial.getInitValue().isEmpty()) {
                int offset = 0;
                for (Integer value : initial.getInitValue()) {
                    new LiAsm(Register.T0, value);
                    new MemTypeAsm("sw", name.substring(1), Register.T0, null, offset);
                    offset += 4;
                }
            }
        }
    }
}
