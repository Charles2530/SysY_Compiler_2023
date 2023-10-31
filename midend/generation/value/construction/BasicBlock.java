package midend.generation.value.construction;

import backend.generation.mips.asm.textsegment.structure.Label;
import midend.generation.value.instr.optimizer.ParallelCopy;
import backend.simplify.method.PhiEliminationUnit;
import iostream.IoStreamGeneration;
import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.irtype.PointerType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.GlobalVar;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.AllocaInstr;
import midend.generation.value.instr.basis.CallInstr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.ActivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.method.BlockSimplifyUnit;
import midend.simplify.method.Mem2RegUnit;

import java.util.ArrayList;
import java.util.HashSet;

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
        instr.setBelongingBlock(this);
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
        instrArrayList.forEach(Instr::generateAssembly);
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
                Mem2RegUnit.dfsVarRename(Mem2RegUnit.getInitialBasicBlock());
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

    public void insertInstr(Integer index, Instr phiInstr) {
        instrArrayList.add(index, phiInstr);
        phiInstr.setBelongingBlock(this);
    }

    public boolean isImprovable(boolean flag) {
        for (Instr instr : instrArrayList) {
            if (instr instanceof CallInstr || instr instanceof IoStreamGeneration) {
                return false;
            }
            for (Value value : instr.getOperands()) {
                if (value instanceof GlobalVar) {
                    return false;
                }
            }
        }
        return flag;
    }

    public void transformPhiInstrToParallelCopy() {
        if (!(instrArrayList.get(0) instanceof PhiInstr)) {
            return;
        }
        ArrayList<ParallelCopy> pcList = new ArrayList<>();
        ArrayList<BasicBlock> indBasicBlock = ControlFlowGraph.getBlockIndBasicBlock(this);
        for (int i = 0; i < indBasicBlock.size(); i++) {
            pcList.add(new ParallelCopy(IrNameController.getLocalVarName(belongingFunc)));
        }
        for (int i = 0; i < indBasicBlock.size(); i++) {
            ArrayList<BasicBlock> outBasicBlock = ControlFlowGraph
                    .getBlockOutBasicBlock(indBasicBlock.get(i));
            if (outBasicBlock.size() == 1) {
                PhiEliminationUnit.putParallelCopy(pcList.get(i), indBasicBlock.get(i));
            } else {
                PhiEliminationUnit.insertParallelCopy(pcList.get(i), indBasicBlock.get(i), this);
            }
        }
        PhiEliminationUnit.removePhiInstr(instrArrayList, pcList);
    }

    public void transformParallelCopyToMoveAsm() {
        if (instrArrayList.size() >= 2 && instrArrayList
                .get(instrArrayList.size() - 2) instanceof ParallelCopy pc) {
            instrArrayList.remove(pc);
            PhiEliminationUnit.getMoveAsm(pc).forEach(
                    move -> insertInstr(instrArrayList.size() - 1, move));
        }
    }

    public void analysisActiveness() {
        HashSet<Value> def = new HashSet<>();
        HashSet<Value> use = new HashSet<>();
        ActivenessAnalysisController.addDefBlockHashSet(this, def);
        ActivenessAnalysisController.addUseBlockHashSet(this, use);
        instrArrayList.forEach(Instr::addPhiToUse);
        instrArrayList.forEach(Instr::genUseDefAnalysis);
    }
}



