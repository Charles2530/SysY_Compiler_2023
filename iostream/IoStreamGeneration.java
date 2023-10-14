package iostream;

import midend.generation.utils.IrType;
import midend.generation.value.construction.user.Instr;

public class IoStreamGeneration extends Instr {

    public IoStreamGeneration(String name, String instrType, IrType type) {
        super(type, name, instrType);

    }
}
