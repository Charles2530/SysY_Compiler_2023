package midend.generation.value.construction;

import backend.generation.mips.asm.textsegment.structure.Label;
import backend.simplify.method.PhiEliminationUnit;
import iostream.structure.IoStreamGeneration;
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
import midend.generation.value.instr.optimizer.ParallelCopy;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;
import midend.simplify.method.BlockSimplifyUnit;
import midend.simplify.method.Mem2RegUnit;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * BasicBlock 是 LLVM IR 中的基本块成分，
 * 继承于Value，主要用于生成基本块
 */
public class BasicBlock extends Value {
    /**
     * instrArrayList 是该 BasicBlock 中的指令集合
     * belongingFunc 是该 BasicBlock 所属的函数
     * exist 是该 BasicBlock 是否存在，主要是用于后
     * 续中间代码优化时删除基本块所使用的标记。
     */
    private ArrayList<Instr> instrArrayList;
    private Function belongingFunc;
    private boolean exist;

    public BasicBlock(String name) {
        super(new StructType("basicBlock"), name);
        this.instrArrayList = new ArrayList<>();
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addBasicBlock(this);
        }
        this.exist = true;
    }

    /**
     * addInstr 方法用于向该 BasicBlock 中添加指令，并且设置该指令所属的基本块
     */
    public void addInstr(Instr instr) {
        instrArrayList.add(instr);
        instr.setBelongingBlock(this);
    }

    /**
     * isEmpty 方法用于判断该 BasicBlock 是否为空
     */
    public boolean isEmpty() {
        return instrArrayList.isEmpty();
    }

    /**
     * getLastInstr 方法用于获取该 BasicBlock 中的最后一条指令
     */
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

    /**
     * simplifyBlock 方法用于简化该 Module 中的所有函数中的基本块，
     * 是中间代码优化基本块优化的处理函数
     */
    public void simplifyBlock() {
        BlockSimplifyUnit.deleteDuplicateBranch(this);
    }

    /**
     * insertPhiProcess 方法用于在 Mem2Reg 优化中插入 Phi 指令
     * 遍历基本块中所有指令并且进行变量重命名
     * 该函数首先需要找出需要添加phiInstr的基本块，
     * 之后通过深度优先搜索进行重命名操作
     */
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

    /**
     * deadCodeElimination 方法用于进行死代码删除
     */
    public void deadCodeElimination() {
        instrArrayList.removeIf(Instr::isDead);
    }

    /**
     * setDeleted 方法用于设置该 BasicBlock 是否存在
     *
     * @return 设置删除标记是否成功
     */
    public boolean setDeleted(boolean exist) {
        this.exist = !exist;
        return true;
    }

    public boolean isExist() {
        return exist;
    }

    /**
     * insertInstr 方法用于在该 BasicBlock 中的指定位置插入指令，
     * 并且设置该指令所属的基本块
     */
    public void insertInstr(Integer index, Instr phiInstr) {
        instrArrayList.add(index, phiInstr);
        phiInstr.setBelongingBlock(this);
    }

    /**
     * isImprovable 方法用于判断该 BasicBlock 是否可以进行 GVN 优化
     * BasicBlock必须满足：每个指令没有读写全局变量，不能调用其他函数
     */
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

    /**
     * transformPhiInstrToParallelCopy 方法用于将该 BasicBlock 中的 Phi 指令
     * 转化为ParallelCopy指令，便于后续转MIPS汇编
     * 该函数执行逻辑如下：
     * 1.先保证block中含有phi指令,否则直接返回
     * 2，遍历block的前驱基本块集合，有多少前驱就增加多少个ParallelCopy指令
     * 3.之后通过for循环寻找合适的位置插入ParallelCopy指令，即
     * 如果indBasicBlock只有block一个后继，那么我们直接将ParallelCopy放在indBasicBlock中即可
     * 如果indBasicBlock有多个后继，那么需要在新建一个中间基本块后插入
     */
    public void transformPhiInstrToParallelCopy() {
        if (!(instrArrayList.get(0) instanceof PhiInstr)) {
            return;
        }
        ArrayList<ParallelCopy> pcList = new ArrayList<>();
        ArrayList<BasicBlock> indBasicBlock = this.getBlockIndBasicBlock();
        for (int i = 0; i < indBasicBlock.size(); i++) {
            pcList.add(new ParallelCopy(IrNameController.getLocalVarName(belongingFunc)));
            ArrayList<BasicBlock> outBasicBlock = indBasicBlock.get(i).getBlockOutBasicBlock();
            if (outBasicBlock.size() == 1) {
                PhiEliminationUnit.putParallelCopy(pcList.get(i), indBasicBlock.get(i));
            } else {
                PhiEliminationUnit.insertParallelCopy(pcList.get(i), indBasicBlock.get(i), this);
            }
        }
        PhiEliminationUnit.removePhiInstr(instrArrayList, pcList);
    }

    /**
     * transformParallelCopyToMoveAsm 方法用于将该 BasicBlock 中的 ParallelCopy 指令
     * 转化为 move 指令，便于后续转MIPS汇编
     * 具体而言即找到基本块中的ParallelCopy指令，删掉ParallelCopy，
     * 之后将ParallelCopy转化为一系列move，最后将move加入到instrList
     */
    public void transformParallelCopyToMoveAsm() {
        if (instrArrayList.size() >= 2 && instrArrayList
                .get(instrArrayList.size() - 2) instanceof ParallelCopy pc) {
            instrArrayList.remove(pc);
            PhiEliminationUnit.getMoveAsm(pc).forEach(
                    move -> insertInstr(instrArrayList.size() - 1, move));
        }
    }

    /**
     * analysisActiveness 方法用于进行活跃变量分析
     * 因为所有的phi指令是并行赋值的，所以其所有的右值都是先使用的
     */
    public void analysisActiveness() {
        HashSet<Value> def = new HashSet<>();
        HashSet<Value> use = new HashSet<>();
        LivenessAnalysisController.addDefBlockHashSet(this, def);
        LivenessAnalysisController.addUseBlockHashSet(this, use);
        instrArrayList.forEach(Instr::addPhiToUse);
        instrArrayList.forEach(Instr::genUseDefAnalysis);
    }

    /**
     * buildFuncCallGraph 方法用于建立函数调用图
     * 该函数调用图用于后续的内联优化
     */
    public void buildFuncCallGraph() {
        instrArrayList.forEach(Instr::buildFuncCallGraph);
    }

    /**
     * getBlockIndBasicBlock 方法用于获取该 BasicBlock 的前序基本块，
     * 主要来自创建ControlFlowGraph的获得的前序基本块分析结果
     */
    public ArrayList<BasicBlock> getBlockIndBasicBlock() {
        return ControlFlowGraph.getBlockIndBasicBlock(this);
    }

    /**
     * getBlockOutBasicBlock 方法用于获取该 BasicBlock 的后序基本块，
     * 主要来自创建ControlFlowGraph的获得的后序基本块分析结果
     */
    public ArrayList<BasicBlock> getBlockOutBasicBlock() {
        return ControlFlowGraph.getBlockOutBasicBlock(this);
    }

    /**
     * getBlockDominateSet 方法用于获取该 BasicBlock 的支配集合，
     * 主要来自创建DominatorTree的获得的支配集合分析结果
     */
    public ArrayList<BasicBlock> getBlockDominateSet() {
        return DominatorTree.getBlockDominateSet(this);
    }

    /**
     * getBlockDominanceFrontier 方法用于获取该 BasicBlock 的支配前沿，
     * 主要来自创建DominatorTree的获得的支配前沿分析结果
     */
    public ArrayList<BasicBlock> getBlockDominanceFrontier() {
        return DominatorTree.getBlockDominanceFrontier(this);
    }

    /**
     * getBlockDominateParent 方法用于获取该 BasicBlock 的支配父节点，
     * 主要来自创建DominatorTree的获得的支配父节点分析结果
     */
    public BasicBlock getBlockDominateParent() {
        return DominatorTree.getBlockDominateParent(this);
    }

    /**
     * getBlockDominateChildList 方法用于获取该 BasicBlock 的支配子节点，
     * 主要来自创建DominatorTree的获得的支配子节点分析结果
     */
    public ArrayList<BasicBlock> getBlockDominateChildList() {
        return DominatorTree.getBlockDominateChildList(this);
    }

    /**
     * getInBasicBlockHashSet 方法用于获取该 BasicBlock 的入口活跃变量集合，
     * 主要来自创建LivenessAnalysisController的获得的入口活跃变量集合分析结果
     */
    public HashSet<Value> getInBasicBlockHashSet() {
        return LivenessAnalysisController.getInBasicBlockHashSet(this);
    }

    /**
     * getOutBasicBlockHashSet 方法用于获取该 BasicBlock 的出口活跃变量集合，
     * 主要来自创建LivenessAnalysisController的获得的出口活跃变量集合分析结果
     */
    public HashSet<Value> getOutBasicBlockHashSet() {
        return LivenessAnalysisController.getOutBasicBlockHashSet(this);
    }

    /**
     * getUseBasicBlockHashSet 方法用于获取该 BasicBlock 的使用活跃变量集合，
     * 主要来自创建LivenessAnalysisController的获得的使用活跃变量集合分析结果
     */
    public HashSet<Value> getUseBasicBlockHashSet() {
        return LivenessAnalysisController.getUseBasicBlockHashSet(this);
    }

    /**
     * getDefBasicBlockHashSet 方法用于获取该 BasicBlock 的定义活跃变量集合，
     * 主要来自创建LivenessAnalysisController的获得的定义活跃变量集合分析结果
     */
    public HashSet<Value> getDefBasicBlockHashSet() {
        return LivenessAnalysisController.getDefBasicBlockHashSet(this);
    }

    /**
     * reducePhi 方法用于将该 BasicBlock 中的 Phi 指令进行简化，
     * 主要是在 Mem2Reg 优化中使用，去除冗余的Phi指令
     */
    public void reducePhi(boolean flag) {
        instrArrayList.forEach(instr -> {
            if (instr instanceof PhiInstr phiInstr) {
                phiInstr.reducePhi(flag);
            }
        });
    }
}



