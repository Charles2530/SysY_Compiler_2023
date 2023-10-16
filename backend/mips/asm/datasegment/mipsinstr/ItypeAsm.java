package backend.mips.asm.datasegment.mipsinstr;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class ItypeAsm extends Assembly {
    private final String operation;
    private final Register rs;
    private final Register rd;
    private final Integer number;

    // addi,addiu,andi,ori,xori,slti,sltiu,lui
    // sll,srl,sra
    public ItypeAsm(String operation, Register rd, Register rs, Integer number) {
        this.operation = operation;
        this.rs = rs;
        this.rd = rd;
        this.number = number;
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return operation + " " + rd + ", " + rs + ", " + number;
    }
}
