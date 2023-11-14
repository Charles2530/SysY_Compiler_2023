package iostream.structure;

import midend.generation.utils.IrType;
import midend.generation.value.construction.user.Instr;

/**
 * IoStreamGeneration 是用于生成LLVM IR的IO流函数的基类
 */
public class IoStreamGeneration extends Instr {

    public IoStreamGeneration(String name, String instrType, IrType type) {
        super(type, name, instrType);
    }
}
