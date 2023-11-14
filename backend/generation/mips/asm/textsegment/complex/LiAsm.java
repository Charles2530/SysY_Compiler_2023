package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * LiAsm 是.text段中的li汇编代码
 * 继承自 Assembly
 */
public class LiAsm extends Assembly {
    /**
     * rd 是目标寄存器
     * number 是立即数
     */
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
