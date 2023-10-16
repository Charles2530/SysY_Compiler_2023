package backend.mips.asm.datasegment.mipsinstr;

import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class JtypeAsm extends Assembly {
    private final String operation;
    private final String label;

    // j,jal
    public JtypeAsm(String operation, String label) {
        this.operation = operation;
        this.label = label;
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return operation + " " + label;
    }
}
