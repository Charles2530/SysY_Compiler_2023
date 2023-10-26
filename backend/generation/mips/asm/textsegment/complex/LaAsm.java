package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class LaAsm extends Assembly {
    private final Register rd;
    private final String label;

    public LaAsm(Register rd, String label) {
        this.rd = rd;
        this.label = label;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "la " + rd + ", " + label;
    }
}
