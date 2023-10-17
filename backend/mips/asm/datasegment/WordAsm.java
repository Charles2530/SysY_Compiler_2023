package backend.mips.asm.datasegment;

import backend.mips.asm.DataAssembly;

public class WordAsm extends DataAssembly {
    public WordAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .word " + value;
    }
}
