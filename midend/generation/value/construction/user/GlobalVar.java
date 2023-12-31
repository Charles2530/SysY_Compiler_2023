package midend.generation.value.construction.user;

import backend.generation.mips.Register;
import backend.generation.mips.asm.datasegment.SpaceAsm;
import backend.generation.mips.asm.datasegment.WordAsm;
import backend.generation.mips.asm.textsegment.complex.LiAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.MemTypeAsm;
import backend.simplify.BackEndOptimizerUnit;
import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.PointerType;
import midend.generation.value.construction.User;
import midend.generation.value.construction.procedure.Initial;

/**
 * GlobalVar 是 LLVM IR 中的全局变量成分，
 * 继承于User，主要用于生成全局变量
 */
public class GlobalVar extends User {
    /**
     * initial 是该 GlobalVar 的初始化器
     * 定义了该全局变量的初始值
     */
    private final Initial initial;

    public GlobalVar(IrType type, String name, Initial initial) {
        super(type, name);
        this.initial = initial;
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addGlobalVar(this);
        }
    }

    public Initial getInitial() {
        return initial;
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
            if (BackEndOptimizerUnit.isSpaceOptimizer()) {
                new SpaceAsm(name.substring(1), String.valueOf(((ArrayType) target)
                        .calcSpaceTot() * 4));
                if (initial.getOffset() != 0) {
                    for (int offset = 0; offset < initial.getOffset(); offset++) {
                        new LiAsm(Register.T0, initial.getInitValue().get(offset));
                        new MemTypeAsm("sw", name.substring(1), Register.T0, null, offset * 4);
                    }
                }
            } else {
                new WordAsm(name.substring(1), String.valueOf(((ArrayType) target)
                        .calcSpaceTot()), initial.getInitValue());
            }
        }
    }
}
