package iostream.declare;

import backend.mips.Register;
import backend.mips.asm.textsegment.complex.LiAsm;
import backend.mips.asm.textsegment.complex.MoveAsm;
import backend.mips.asm.textsegment.mipsinstr.SyscallAsm;
import backend.utils.AssemblyUnit;
import backend.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import iostream.IoStreamGeneration;

public class GetIntDeclare extends IoStreamGeneration {
    public GetIntDeclare(String name, String instrType) {
        super(name, instrType, new VarType(32));
    }

    public static String getDeclare() {
        return "declare i32 @getint() ";
    }

    @Override
    public String toString() {
        return name + " = call i32 @getint()";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        new LiAsm(Register.V0, 5);
        new SyscallAsm();
        Register reg = AssemblyUnit.getRegisterController().getRegister(this);
        if (reg != null) {
            new MoveAsm(reg, Register.V0);
        } else {
            RegisterUtils.reAllocReg(this, Register.V0);
        }
    }
}
