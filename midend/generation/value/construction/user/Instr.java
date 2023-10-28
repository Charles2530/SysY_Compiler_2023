package midend.generation.value.construction.user;

import backend.generation.mips.asm.textsegment.structure.Comment;
import iostream.IoStreamGeneration;
import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.User;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CalcInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.basis.GetEleInstr;
import midend.generation.value.instr.basis.IcmpInstr;
import midend.generation.value.instr.basis.LoadInstr;
import midend.generation.value.instr.basis.ZextInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.method.Mem2RegUnit;

public class Instr extends User {
    protected String instrType;
    private BasicBlock parent;

    public Instr(IrType type, String name, String instrType) {
        super(type, name);
        this.instrType = instrType;
        if (!OptimizerUnit.isIsOptimizer()) {
            IrNameController.addInstr(this);
        }
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

    public boolean isDead() {
        return this.isValid() && !(this instanceof CallInstr) &&
                !(this instanceof IoStreamGeneration) && this.getUseDefChain().isEmpty();
    }

    private boolean isValid() {
        boolean valid = this instanceof AllocaInstr || this instanceof CalcInstr ||
                (this instanceof CallInstr callInstr && !callInstr.getType().isVoid()) ||
                this instanceof IoStreamGeneration;
        return valid || this instanceof PhiInstr ||
                this instanceof GetEleInstr || this instanceof IcmpInstr ||
                this instanceof LoadInstr || this instanceof ZextInstr;
    }
}
