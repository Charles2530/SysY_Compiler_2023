package backend.mips.asm.textsegment.structure;

import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class Label extends Assembly {
    private final String label;

    public Label(String label) {
        this.label = label;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
