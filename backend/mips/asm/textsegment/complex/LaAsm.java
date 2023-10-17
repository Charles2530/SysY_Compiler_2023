package backend.mips.asm.textsegment.complex;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

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
