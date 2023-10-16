package backend.mips.asm.datasegment.mipsinstr;

import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class SyscallAsm extends Assembly {
    public SyscallAsm() {
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
