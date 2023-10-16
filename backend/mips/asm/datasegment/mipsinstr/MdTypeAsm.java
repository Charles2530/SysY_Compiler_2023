package backend.mips.asm.datasegment.mipsinstr;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class MdTypeAsm extends Assembly {
    private final String operation;
    private final Register rd;
    private final Register rs;
    private final Register rt;

    // mul,div,mflo,mfhi,mthi,mtlo
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
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return (rd == null) ? operation + " " + rs + " " + rt :
                operation + " " + rd;
    }
}
