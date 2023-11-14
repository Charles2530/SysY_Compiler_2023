package backend.generation.mips.asm.datasegment;

import backend.generation.mips.asm.DataAssembly;
/**
 * AsciizAsm 是.data段中的.asciiz汇编代码
 * 继承自 DataAssembly
 * */
public class AsciizAsm extends DataAssembly {
    public AsciizAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .asciiz \"" + value.replace("\n", "\\n") + "\"";
    }
}
