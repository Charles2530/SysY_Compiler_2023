package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * BtypeAsm 是.text段中的b系列汇编代码
 * 继承自 Assembly
 */
public class BtypeAsm extends Assembly {
    /**
     * operation 是汇编代码的操作
     * 包括 beq,bne,bgtz,blez,bgez,bltz
     * rs 是源寄存器rs
     * rt 是源寄存器rt
     * label 是标签
     */
    private final String operation;
    private final Register rs;
    private final Register rt;
    private final String label;

    public BtypeAsm(String operation, String label, Register... registers) {
        this.operation = operation;
        this.label = label;
        if (registers.length == 2) {
            this.rs = registers[0];
            this.rt = registers[1];
        } else {
            this.rs = registers[0];
            this.rt = null;
        }
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (rt == null) ? operation + " " + rs + " " + label :
                operation + " " + rs + " " + rt + " " + label;
    }
}
