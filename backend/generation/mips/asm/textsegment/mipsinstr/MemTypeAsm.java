package backend.generation.mips.asm.textsegment.mipsinstr;

import backend.generation.mips.Register;
import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class MemTypeAsm extends Assembly {
    private final String operation;
    private final String label;
    private final Register rd;
    private final Register base;
    private final Integer offset;

    // lb,lbu,lh,lhu,lw,sb,sh,sw
    public MemTypeAsm(String operation, String label, Register rd, Register base, Integer offset) {
        this.operation = operation;
        this.label = label;
        this.rd = rd;
        this.base = base;
        this.offset = offset;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return (base == null) ? operation + " " + rd + " " + label + "+" + offset :
                operation + " " + rd + " " + offset + "(" + base + ")";
    }
}
