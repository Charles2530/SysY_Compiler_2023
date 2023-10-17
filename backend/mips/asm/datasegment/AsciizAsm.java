package backend.mips.asm.datasegment;

import backend.mips.asm.DataAssembly;

public class AsciizAsm extends DataAssembly {
    public AsciizAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .asciiz \"" + value.replace("\n", "\\n") + "\"";
    }
}
