package backend.generation.mips.asm.textsegment.structure;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * Label 是.text段中的标签
 * 继承自 Assembly
 */
public class Label extends Assembly {
    /**
     * label 是标签名
     */
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
