package backend.generation.mips.asm.datasegment;

import backend.generation.mips.asm.DataAssembly;

/**
 * SpaceAsm 是.data段中的.space汇编代码
 * 继承自 DataAssembly
 */
public class SpaceAsm extends DataAssembly {

    public SpaceAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .space " + value;
    }
}
