package backend.mips.asm.textsegment.mipsinstr;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class RtypeAsm extends Assembly {
    private final String operation;
    private final Register rs;
    private final Register rt;
    private final Register rd;

    // add,sub,addu,subu,and,or,xor,nor,slt,sltu
    // sllv,srlv,srav
    // slt,sle,sgt,sge,seq,sne
    // jr
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
