package backend.generation.mips.asm.textsegment.complex;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * MoveAsm 是.text段中的move汇编代码
 * 继承自 Assembly
 */
public class MoveAsm extends Assembly {
    /**
     * dst 是目标寄存器
     * src 是源寄存器
     */
    private final Register dst;
    private final Register src;

    public MoveAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "move " + dst + " " + src;
    }
}
