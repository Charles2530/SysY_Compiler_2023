package midend.generation.value.construction.user;

import backend.generation.mips.asm.textsegment.structure.Comment;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.User;
import midend.simplify.method.Mem2RegUnit;

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

    public BasicBlock getBelongingBlock() {
        return parent;
    }

    @Override
    public void generateAssembly() {
        new Comment(this.toString());
    }

    public void insertPhiProcess() {
        Mem2RegUnit.reConfig(this);
        Mem2RegUnit.insertPhi();
    }

    /*TODO:judge need to change*/
    public boolean isDead() {
        return false;
    }
}
