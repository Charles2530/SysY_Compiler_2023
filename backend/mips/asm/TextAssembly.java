package backend.mips.asm;

import backend.utils.AssemblyData;

public class TextAssembly extends Assembly {
    protected String label;
    protected String value;

    public TextAssembly(String label, String value) {
        this.label = label;
        this.value = value;
        AssemblyData.addTextAssembly(this);
    }
}
