package backend.generation.mips.asm.textsegment.structure;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

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
