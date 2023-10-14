package midend.generation.value.construction.user;

import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.User;

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
