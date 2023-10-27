package midend.generation.value.construction;

import backend.generation.mips.asm.textsegment.structure.Label;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.simplify.method.BlockSimplifyUnit;
import midend.simplify.method.Mem2RegUnit;

import java.util.ArrayList;
import java.util.Iterator;

public class BasicBlock extends Value {
    private ArrayList<Instr> instrArrayList;
    private Function belongingFunc;
    private ArrayList<BasicBlock> indBasicBlocks;
    private ArrayList<BasicBlock> outBasicBlocks;
    private ArrayList<BasicBlock> dominateSet;
    private ArrayList<BasicBlock> dominanceFrontier;
    private ArrayList<BasicBlock> childList;
    private BasicBlock parent;

    public BasicBlock(String name) {
        super(new StructType("basicblock"), name);
        this.instrArrayList = new ArrayList<>();
        IrNameController.addBasicBlock(this);
    }

    public void addInstr(Instr instr) {
        instrArrayList.add(instr);
    }

    public boolean isEmpty() {
        return instrArrayList.isEmpty();
    }

    public Instr getLastInstr() {
        return instrArrayList.get(instrArrayList.size() - 1);
    }

    public void setInstrArrayList(ArrayList<Instr> instrArrayList) {
        this.instrArrayList = instrArrayList;
    }

    public ArrayList<Instr> getInstrArrayList() {
        return instrArrayList;
    }

    public Function getBelongingFunc() {
        return belongingFunc;
    }

    public void setBelongingFunc(Function belongingFunc) {
        this.belongingFunc = belongingFunc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n\t");
        for (int i = 0; i < instrArrayList.size(); i++) {
            sb.append(instrArrayList.get(i).toString());
            if (i != instrArrayList.size() - 1) {
                sb.append("\n\t");
            }
        }
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        new Label(name);
        for (Instr instr : instrArrayList) {
            instr.generateAssembly();
        }
    }

    public void simplifyBlock() {
        BlockSimplifyUnit.deleteDuplicateBranch(this);
    }

    public void insertPhiProcess() {
        for (Instr instr : instrArrayList) {
            if (instr instanceof AllocaInstr &&
                    ((PointerType) instr.getType()).getTarget().isInt32()) {
                instr.insertPhiProcess();
                Mem2RegUnit.varRename();
            }
        }
    }

    public void deadCodeElimination() {
        Iterator<Instr> iter = instrArrayList.iterator();
        while (iter.hasNext()) {
            Instr instr = iter.next();
            if (instr.isDead()) {
                iter.remove();
            }
        }
    }

    public void setIndBasicBlocks(ArrayList<BasicBlock> indBasicBlocks) {
        this.indBasicBlocks = indBasicBlocks;
    }

    public void setOutBasicBlocks(ArrayList<BasicBlock> outBasicBlocks) {
        this.outBasicBlocks = outBasicBlocks;
    }

    public ArrayList<BasicBlock> getIndBasicBlocks() {
        return indBasicBlocks;
    }

    public ArrayList<BasicBlock> getOutBasicBlocks() {
        return outBasicBlocks;
    }

    public void setDominateSet(ArrayList<BasicBlock> domList) {
        this.dominateSet = domList;
    }

    public ArrayList<BasicBlock> getDominateSet() {
        return dominateSet;
    }

    public void setDominanceFrontier(ArrayList<BasicBlock> dominanceFrontier) {
        this.dominanceFrontier = dominanceFrontier;
    }

    public void updateDominateTree(BasicBlock parent, ArrayList<BasicBlock> childList) {
        this.parent = parent;
        this.childList = childList;
    }

    public BasicBlock getParent() {
        return parent;
    }
}
