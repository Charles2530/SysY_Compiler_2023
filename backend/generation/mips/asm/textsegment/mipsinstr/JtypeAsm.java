package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class JtypeAsm extends Assembly {
    private final String operation;
    private final String label;

    // j,jal
    public JtypeAsm(String operation, String label) {
        this.operation = operation;
        this.label = label;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return operation + " " + label;
    }
}
