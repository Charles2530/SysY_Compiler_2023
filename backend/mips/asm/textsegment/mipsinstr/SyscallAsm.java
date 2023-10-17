package backend.mips.asm.textsegment.mipsinstr;

import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class SyscallAsm extends Assembly {
    public SyscallAsm() {
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
