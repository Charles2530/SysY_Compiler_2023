package backend.generation.mips.asm.datasegment;

import backend.generation.mips.asm.DataAssembly;

public class SpaceAsm extends DataAssembly {

    public SpaceAsm(String label, String value) {
        super(label, value);
    }

    @Override
    public String toString() {
        return label + ": .space " + value;
    }
}
