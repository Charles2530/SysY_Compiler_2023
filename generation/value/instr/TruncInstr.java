package generation.value.instr;

import generation.utils.irtype.VarType;
import generation.value.construction.user.Instr;

public class TruncInstr extends Instr {
    public TruncInstr(String name, String instrType) {
        super(new VarType(32), name, instrType);
    }
}
