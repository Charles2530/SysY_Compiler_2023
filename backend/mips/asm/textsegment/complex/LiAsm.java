package backend.mips.asm.textsegment.complex;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class LiAsm extends Assembly {
    private final Register rd;
    private final Integer number;

    public LiAsm(Register rd, Integer number) {
        this.rd = rd;
        this.number = number;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "li " + rd + ", " + number;
    }
}
