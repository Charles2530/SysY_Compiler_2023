package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * SyscallAsm 是.text段中的syscall汇编代码
 * 继承自 Assembly
 */
public class SyscallAsm extends Assembly {
    public SyscallAsm() {
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
