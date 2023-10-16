package backend.mips.asm.datasegment.mipsinstr;

import backend.mips.Register;
import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class BtypeAsm extends Assembly {
    private final String operation;
    private final Register rs;
    private final Register rt;
    private final String label;

    // beq,bne,bgtz,blez,bgez,bltz
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
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return (rt == null) ? operation + " " + rs + " " + label :
                operation + " " + rs + " " + rt + " " + label;
    }
}
