package backend.mips.asm.textsegment;

import backend.mips.asm.TextAssembly;

public class SpaceAsm extends TextAssembly {
    public SpaceAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .space " + value;
    }
}
