package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * JtypeAsm 是.text段中的j,jal汇编代码
 * 继承自 Assembly
 */
public class JtypeAsm extends Assembly {
    /**
     * operation 是操作符
     * label 是标签
     */
    private final String operation;
    private final String label;

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
