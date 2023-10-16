package backend.mips.asm.datasegment.structure;

import backend.mips.asm.Assembly;
import backend.utils.AssemblyData;

public class Comment extends Assembly {
    private final String comment;

    public Comment(String comment) {
        this.comment = comment;
        AssemblyData.addDataAssembly(this);
    }

    @Override
    public String toString() {
        return "\n# " + comment;
    }
}
