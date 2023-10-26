package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class MoveAsm extends Assembly {
    private final Register dst;
    private final Register src;

    public MoveAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "move " + dst + ", " + src;
    }
}
