package midend.generation.value.construction;

import backend.generation.mips.asm.textsegment.structure.Label;
import iostream.OptimizerUnit;
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

public class BasicBlock extends Value {
    private ArrayList<Instr> instrArrayList;
    private Function belongingFunc;
    private boolean exist;

    public BasicBlock(String name) {
        super(new StructType("basicblock"), name);
        this.instrArrayList = new ArrayList<>();
        if (!OptimizerUnit.isIsOptimizer()) {
            IrNameController.addBasicBlock(this);
        }
        this.exist = true;
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
        ArrayList<Instr> copy = new ArrayList<>(instrArrayList);
        for (Instr instr : copy) {
            if (instr instanceof AllocaInstr &&
                    ((PointerType) instr.getType()).getTarget().isInt32()) {
                instr.insertPhiProcess();
                Mem2RegUnit.varRename(Mem2RegUnit.getInitialBasicBlock());
            }
        }
    }

    public void deadCodeElimination() {
        instrArrayList.removeIf(Instr::isDead);
    }

    public boolean setDeleted(boolean exist) {
        this.exist = !exist;
        return true;
    }

    public boolean isExist() {
        return exist;
    }

    public void addInstrToFirst(Instr phiInstr) {
        instrArrayList.add(0, phiInstr);
    }
}
