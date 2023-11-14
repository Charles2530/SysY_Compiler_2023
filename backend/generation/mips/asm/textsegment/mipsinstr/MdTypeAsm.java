package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * MdTypeAsm 是.text段中的mult,div,mflo,mfhi,mthi,mtlo汇编代码
 * 继承自 Assembly
 */
public class MdTypeAsm extends Assembly {
    /**
     * operation 是操作符
     * rd,rs,rt 是寄存器
     */
    private final String operation;
    private final Register rd;
    private final Register rs;
    private final Register rt;

    public MdTypeAsm(String operation, Register... registers) {
        this.operation = operation;
        if (registers.length == 2) {
            this.rd = null;
            this.rs = registers[0];
            this.rt = registers[1];
        } else {
            this.rd = registers[0];
            this.rs = null;
            this.rt = null;
        }
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (rd == null) ? operation + " " + rs + " " + rt :
                operation + " " + rd;
    }
}
