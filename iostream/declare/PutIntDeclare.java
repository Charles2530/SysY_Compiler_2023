package iostream.declare;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.LiAsm;
import backend.generation.mips.asm.textsegment.complex.MoveAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.SyscallAsm;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterUtils;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import iostream.structure.IoStreamGeneration;

/**
 * PutIntDeclare 是向控制台输出一个整数的声明
 * 是生成LLVM IR的函数声明
 * 继承自 IoStreamGeneration
 */
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
        targetReg = RegisterUtils.loadVariableValue(operands.get(0), targetReg, Register.A0);
        if (targetReg != Register.A0) {
            new MoveAsm(Register.A0, targetReg);
        }
        new LiAsm(Register.V0, 1);
        new SyscallAsm();
    }
}
