package backend.generation.mips.asm.textsegment.structure;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

public class Comment extends Assembly {
    private final String comment;

    public Comment(String comment) {
        this.comment = comment;
        AssemblyData.addTextAssembly(this);
    }

    @Override
    public String toString() {
        return "\n# " + comment;
    }
}
