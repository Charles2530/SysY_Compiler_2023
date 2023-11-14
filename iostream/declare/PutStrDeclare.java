package iostream.declare;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.complex.LaAsm;
import backend.generation.mips.asm.textsegment.complex.LiAsm;
import backend.generation.mips.asm.textsegment.mipsinstr.SyscallAsm;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.FormatString;
import iostream.structure.IoStreamGeneration;

/**
 * PutStrDeclare 是向控制台输出一个字符串的声明
 * 是生成LLVM IR的函数声明
 * 继承自 IoStreamGeneration
 */
public class PutStrDeclare extends IoStreamGeneration {
    private final FormatString str;

    public PutStrDeclare(FormatString str) {
        super("PutStrDeclare", "io", new VarType(0));
        this.str = str;
    }

    public static String getDeclare() {
        return "declare void @putch(i32)\ndeclare void @putstr(i8*)";
    }

    @Override
    public String toString() {
        return "call void @putstr(i8* getelementptr inbounds (" +
                str.getPointer().getTarget() + ", " +
                str.getPointer() + " " +
                str.getName() + ", i64 0, i64 0))";
    }

    @Override
    public void generateAssembly() {
        super.generateAssembly();
        new LaAsm(Register.A0, str.getName().substring(1));
        new LiAsm(Register.V0, 4);
        new SyscallAsm();
    }
}
