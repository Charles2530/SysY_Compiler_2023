package backend.generation.mips.asm.textsegment.structure;

import backend.generation.mips.asm.Assembly;
import backend.generation.utils.AssemblyData;

/**
 * Comment 是.text段中的注释
 * 继承自 Assembly
 */
public class Comment extends Assembly {
    /**
     * comment 是注释内容
     * 这里用于在每次生成汇编代码时，添加对应的
     * LLVM IR中间代码，便于对拍
     */
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
