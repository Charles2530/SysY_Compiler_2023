package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

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
        return "li " + rd + " " + number;
    }
}
