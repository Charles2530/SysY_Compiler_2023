package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * RtypeAsm 是.text段中的R型汇编代码
 * 继承自 Assembly
 */
public class RtypeAsm extends Assembly {
    /**
     * operation 是操作符
     * 包括 add,sub,addu,subu,and,or,xor,nor,slt,sltu
     * sllv,srlv,srav
     * slt,sle,sgt,sge,seq,sne
     * jr
     * rs,rt,rd 是寄存器
     */
    private final String operation;
    private final Register rs;
    private final Register rt;
    private final Register rd;

    public RtypeAsm(String operation, Register... registers) {
        this.operation = operation;
        if (registers.length == 3) {
            this.rd = registers[0];
            this.rs = registers[1];
            this.rt = registers[2];
        } else {
            this.rd = registers[0];
            this.rs = null;
            this.rt = null;
        }
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (rs == null && rt == null) ? operation + " " + rd :
                operation + " " + rd + " " + rs + " " + rt;
    }
}
