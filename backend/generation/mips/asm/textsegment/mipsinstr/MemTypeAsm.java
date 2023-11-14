package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * MemTypeAsm 是.text段中的存储类汇编代码
 * 继承自 Assembly
 */
public class MemTypeAsm extends Assembly {
    /**
     * operation 是操作符
     * 包括 lb,lbu,lh,lhu,lw,sb,sh,sw
     * label 是标签
     * rd 是目标寄存器
     * base 是基址寄存器
     * offset 是偏移量
     */
    private final String operation;
    private final String label;
    private final Register rd;
    private final Register base;
    private final Integer offset;

    public MemTypeAsm(String operation, String label, Register rd, Register base, Integer offset) {
        this.operation = operation;
        this.label = label;
        this.rd = rd;
        this.base = base;
        this.offset = offset;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (base == null) ? operation + " " + rd + " " + label + "+" + offset :
                operation + " " + rd + " " + offset + "(" + base + ")";
    }
}
