package backend.mips.asm.datasegment.complex;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class MoveAsm extends Assembly {
    private final Register dst;
    private final Register src;

    public MoveAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return "move " + dst + ", " + src;
    }
}
