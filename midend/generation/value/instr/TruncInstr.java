package midend.generation.value.instr;

import midend.generation.utils.irtype.VarType;
import midend.generation.value.construction.user.Instr;

public class TruncInstr extends Instr {
    public TruncInstr(String name, String instrType) {
        super(new VarType(32), name, instrType);
    }
}
