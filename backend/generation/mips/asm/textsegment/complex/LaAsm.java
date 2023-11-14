package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * LaAsm 是.text段中的la汇编代码
 * 继承自 Assembly
 */
public class LaAsm extends Assembly {
    /**
     * rd 是目标寄存器
     * label 是标签
     */
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
