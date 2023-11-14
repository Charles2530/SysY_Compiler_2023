package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * ItypeAsm 是.text段中的I型指令汇编代码
 * 继承自 Assembly
 */
public class ItypeAsm extends Assembly {
    /**
     * operation 是指令名
     * 包括 addi,addiu,andi,ori,xori,slti,sltiu,lui
     * sll,srl,sra
     * rs 是源寄存器
     * rd 是目标寄存器
     * number 是立即数
     */
    private final String operation;
    private final Register rs;
    private final Register rd;
    private final Integer number;

    public ItypeAsm(String operation, Register rd, Register rs, Integer number) {
        this.operation = operation;
        this.rs = rs;
        this.rd = rd;
        this.number = number;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return operation + " " + rd + " " + rs + " " + number;
    }
}
