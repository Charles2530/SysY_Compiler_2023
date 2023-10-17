package iostream.declare;

import backend.mips.Register;
import backend.mips.asm.datasegment.complex.LiAsm;
import backend.mips.asm.datasegment.complex.MoveAsm;
import backend.mips.asm.datasegment.mipsinstr.SyscallAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import iostream.IoStreamGeneration;

public class PutIntDeclare extends IoStreamGeneration {
    public PutIntDeclare(Value target) {
        super("PutIntDeclare", "io", new VarType(0));
        addOperand(target);
    }

    public static String getDeclare() {
        return "declare void @putint(i32)";
    }

    @Override
    public String toString() {
        return "call void @putint(i32 " + operands.get(0).getName() + ")";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        Register targetReg = AssemblyUnit.getRegisterController().getRegister(operands.get(0));
        targetReg = RegisterUtils.loadRegVal(operands.get(0), targetReg, Register.A0);
        if (targetReg != Register.A0) {
            new MoveAsm(Register.A0, targetReg);
        }
        new LiAsm(Register.V0, 1);
        new SyscallAsm();
    }
}
