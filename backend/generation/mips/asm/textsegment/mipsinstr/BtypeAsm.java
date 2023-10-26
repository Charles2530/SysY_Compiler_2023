package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

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
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (rt == null) ? operation + " " + rs + " " + label :
                operation + " " + rs + " " + rt + " " + label;
    }
}
