package backend.mips.asm.datasegment.complex;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class LiAsm extends Assembly {
    private final Register rd;
    private final Integer number;

    public LiAsm(Register rd, Integer number) {
        this.rd = rd;
        this.number = number;
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return "li " + rd + ", " + number;
    }
}
