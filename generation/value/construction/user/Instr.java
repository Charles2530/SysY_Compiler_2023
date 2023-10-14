package generation.value.construction.user;

import generation.utils.IrNameController;
import generation.utils.IrType;
import generation.value.construction.BasicBlock;
import generation.value.construction.User;

public class Instr extends User {
    protected String instrType;
    private BasicBlock parent;

    public Instr(IrType type, String name, String instrType) {
        super(type, name);
        this.instrType = instrType;
        IrNameController.addInstr(this);
    }

    public void setBelongingBlock(BasicBlock currentBasicBlock) {
        this.parent = currentBasicBlock;
    }
}
