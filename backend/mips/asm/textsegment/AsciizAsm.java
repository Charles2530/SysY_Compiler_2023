package backend.mips.asm.textsegment;

import backend.mips.asm.TextAssembly;

public class AsciizAsm extends TextAssembly {
    public AsciizAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .asciiz " + value;
    }
}
