package backend.generation.mips.asm;

import backend.generation.utils.AssemblyData;

/**
 * DataAssembly 是.data段中的汇编代码
 */
public class DataAssembly extends Assembly {
    /**
     * label 是汇编代码的标签
     * value 是汇编代码的值
     */
    protected String label;
    protected String value;

    public DataAssembly(String label, String value) {
        this.label = label;
        this.value = value;
        AssemblyData.addDataAssembly(this);
    }
}
