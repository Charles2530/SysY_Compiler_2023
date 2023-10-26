package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class SyscallAsm extends Assembly {
    public SyscallAsm() {
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
