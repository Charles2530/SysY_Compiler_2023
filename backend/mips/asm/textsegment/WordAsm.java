package backend.mips.asm.textsegment;

import backend.mips.asm.TextAssembly;

public class WordAsm extends TextAssembly {
    public WordAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .word " + value;
    }
}
