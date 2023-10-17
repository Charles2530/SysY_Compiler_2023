package backend.mips.asm;

import backend.utils.AssemblyData;

public class DataAssembly extends Assembly {
    protected String label;
    protected String value;

    public DataAssembly(String label, String value) {
        this.label = label;
        this.value = value;
        AssemblyData.addDataAssembly(this);
    }
}
