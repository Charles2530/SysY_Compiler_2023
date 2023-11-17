package midend.generation.value.construction.user;

import backend.generation.mips.Register;
import backend.generation.mips.asm.textsegment.structure.Label;
import backend.generation.utils.AssemblyUnit;
import backend.generation.utils.RegisterAllocator;
import backend.generation.utils.RegisterUtils;
import backend.simplify.BackEndOptimizerUnit;
import backend.simplify.method.BasicBlockSortedUnit;
import iostream.structure.DebugDetailController;
import iostream.OptimizerUnit;
import midend.generation.utils.IrNameController;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.StructType;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Param;
import midend.generation.value.construction.User;
import midend.generation.value.instr.basis.CallInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;
import midend.simplify.method.FunctionInlineUnit;
import midend.simplify.method.GlobalCodeMovementUnit;
import midend.simplify.method.GlobalVariableNumberingUnit;
import midend.simplify.method.Mem2RegUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Function 是 LLVM IR 中的函数成分，
 * 继承于User，主要用于生成函数
 */
public class Function extends User {
    /**
     * returnType 是该 Function 的返回类型
     * basicBlocks 是该 Function 的基本块集合
     * params 是该 Function 的参数集合
     * registerHashMap 是该 Function 的寄存器映射表
     * isImproved 是该 Function 是否可以进行 GVN 优化
     */
    private final IrType returnType;
    private final ArrayList<BasicBlock> basicBlocks;
    private final ArrayList<Param> params;
    private HashMap<Value, Register> registerHashMap;
    private Boolean isImproved;

    public Function(String name, IrType returnType) {
        super(new StructType("function"), name);
        this.returnType = returnType;
        this.basicBlocks = new ArrayList<>();
        this.params = new ArrayList<>();
        this.registerHashMap = null;
        this.isImproved = null;
        if (!OptimizerUnit.isOptimizer()) {
            IrNameController.addFunction(this);
        }
    }

    public IrType getReturnType() {
        return returnType;
    }

    /**
     * addBasicBlock 方法用于向该 Function 中添加基本块
     *
     * @param basicBlock 是要添加的基本块
     * @param idx        是要添加的位置
     */
    public void addBasicBlock(BasicBlock basicBlock, int... idx) {
        if (idx.length == 1) {
            basicBlocks.add(idx[0], basicBlock);
        } else {
            basicBlocks.add(basicBlock);
        }
        basicBlock.setBelongingFunc(this);
    }

    public ArrayList<Param> getParams() {
        return params;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    /**
     * addParam 方法用于向该 Function 中添加参数
     */
    public void addParam(Param param) {
        params.add(param);
        param.setBelongingFunc(this);
    }

    public HashMap<Value, Register> getRegisterHashMap() {
        return registerHashMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(returnType.toString())
                .append(" ").append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).toString());
            if (i != params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") {\n\n");
        for (int i = 0; i < basicBlocks.size(); i++) {
            sb.append(basicBlocks.get(i).toString());
            if (i != basicBlocks.size() - 1) {
                sb.append("\n\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    @Override
    public void generateAssembly() {
        new Label(name.substring(1));
        AssemblyUnit.resetFunctionConfig(this);
        for (int i = 0; i < Math.min(3, params.size()); i++) {
            AssemblyUnit.getRegisterController().allocRegister(params.get(i),
                    Register.regTransform(Register.A0.ordinal() + i + 1));
            RegisterUtils.moveValueOffset(params.get(i));
        }
        for (int i = 3; i < params.size(); i++) {
            RegisterUtils.moveValueOffset(params.get(i));
        }
        if (BackEndOptimizerUnit.isBasicBlockSorted()) {
            BasicBlockSortedUnit.sort(basicBlocks);
        }
        basicBlocks.forEach(BasicBlock::generateAssembly);
    }

    /**
     * simplifyBlock 方法用于简化该 Function 中的所有基本块，
     * 主要用于基本块优化
     */
    public void simplifyBlock() {
        basicBlocks.forEach(BasicBlock::simplifyBlock);
    }

    /**
     * insertPhiProcess 方法用于在该 Function 中的所有基本块中插入 Phi 指令，
     * 主要用于 Mem2Reg 优化
     * 遍历所有基本块，插入phi指令,同时注意设置InitialBasicBlock
     */
    public void insertPhiProcess() {
        Mem2RegUnit.setInitialBasicBlock(basicBlocks.get(0));
        basicBlocks.forEach(BasicBlock::insertPhiProcess);
    }

    /**
     * deadCodeElimination 方法用于在该 Function 中的所有基本块中进行死代码删除，
     * 主要用于死代码删除优化
     */
    public void deadCodeElimination() {
        basicBlocks.forEach(BasicBlock::deadCodeElimination);
    }

    /**
     * searchBlockDominateSet 方法用于在该 Function 中的所有基本块中进行支配集搜索，
     * 主要用于支配树的构建
     * 对于这个函数而言，函数的逻辑是在for循环中找到所有不被 basicBlock支配的block，
     * 放入reachSet中，这样所有不在reachedSet中的Block就都是被basicBlock
     * 支配的Block，然后将所有不在reachSet中的block放入domList中(包括basicBlock本身)
     */
    public void searchBlockDominateSet() {
        BasicBlock entry = basicBlocks.get(0);
        for (BasicBlock basicBlock : basicBlocks) {
            HashSet<BasicBlock> reachedSet = new HashSet<>();
            DominatorTree.dfsDominate(entry, basicBlock, reachedSet);
            ArrayList<BasicBlock> domList = new ArrayList<>();
            for (BasicBlock bb : basicBlocks) {
                if (!reachedSet.contains(bb)) {
                    domList.add(bb);
                }
            }
            DominatorTree.addBlockDominateSet(basicBlock, domList);
        }
    }

    /**
     * searchBlockDominanceFrontier 方法用于在该 Function 中的所有基本块中进行支配边搜索，
     * 主要用于支配树的构建
     * 求解DominanceFrontier是一个固定的算法,对所有的边进行遍历即可
     */
    public void searchBlockDominanceFrontier() {
        for (Map.Entry<BasicBlock, ArrayList<BasicBlock>> entry :
                ControlFlowGraph.getFunctionOutBasicBlock(this).entrySet()) {
            BasicBlock from = entry.getKey();
            ArrayList<BasicBlock> outBasicBlocks = entry.getValue();
            for (BasicBlock to : outBasicBlocks) {
                BasicBlock runner = from;
                while (!runner.getBlockDominateSet().contains(to)
                        || runner.equals(to)) {
                    DominatorTree.addBlockDominanceFrontierEdge(runner, to);
                    runner = runner.getBlockDominateParent();
                }
            }
        }
    }

    /**
     * uniqueInstr 方法用于找出Block中可以优化的指令,
     * 主要用于 GVN 优化
     */
    public void uniqueInstr() {
        HashMap<String, Instr> hashMap = new HashMap<>();
        GlobalVariableNumberingUnit.uniqueInstr(basicBlocks.get(0), hashMap);
        GlobalVariableNumberingUnit.addHashMap(this, hashMap);
    }

    /**
     * isImprovable 方法用于判断该 Function 是否可以进行 GVN 优化
     * Function必须满足：参数没有指针类型, 每个指令没有读写全局变量，不能调用其他函数
     */
    public boolean isImprovable() {
        boolean flag = true;
        if (isImproved != null) {
            return isImproved;
        }
        for (Param param : params) {
            if (param.getType().isPointer()) {
                flag = false;
                break;
            }
        }
        for (BasicBlock basicBlock : basicBlocks) {
            if (!(flag = basicBlock.isImprovable(flag))) {
                break;
            }
        }
        isImproved = flag;
        return flag;
    }

    /**
     * analysisActiveness 方法用于在该 Function 中的所有基本块中进行活跃变量分析，
     * 主要用于活跃变量分析
     */
    public void analysisActiveness() {
        HashMap<BasicBlock, HashSet<Value>> inMap = new HashMap<>();
        HashMap<BasicBlock, HashSet<Value>> outMap = new HashMap<>();
        LivenessAnalysisController.addInFunctionHashMap(this, inMap);
        LivenessAnalysisController.addOutFunctionHashMap(this, outMap);
        for (BasicBlock basicBlock : basicBlocks) {
            LivenessAnalysisController.addInBlockHashSet(basicBlock, new HashSet<>());
            LivenessAnalysisController.addOutBlockHashSet(basicBlock, new HashSet<>());
        }
        basicBlocks.forEach(BasicBlock::analysisActiveness);
        LivenessAnalysisController.calculateInOut(this);
    }

    /**
     * regAllocate 方法用于在该 Function 中的所有基本块中进行寄存器分配，
     * 主要用于寄存器分配
     */
    public void regAllocate() {
        BasicBlock entry = basicBlocks.get(0);
        RegisterAllocator.setRegisterHashMap(new HashMap<>());
        RegisterAllocator.setValueHashMap(new HashMap<>());
        RegisterAllocator.blockAllocate(entry);
        this.registerHashMap = RegisterAllocator.getRegisterHashMap();
        DebugDetailController.printRegisterValueReflection(this, registerHashMap);
    }

    /**
     * phiEliminate 方法用于在该 Function 中的所有基本块中进行 Phi 指令消除，
     * 主要用于 Phi 指令消除，便于后续转MIPS汇编
     * 这里消除Phi指令有两个步骤
     * 1.将所有的phi指令删除，增加ParallelCopy指令
     * 2.将所有的ParallelCopy指令删除，增加move指令
     */
    public void phiEliminate() {
        ArrayList<BasicBlock> copy = new ArrayList<>(basicBlocks);
        copy.forEach(BasicBlock::transformPhiInstrToParallelCopy);
        basicBlocks.forEach(BasicBlock::transformParallelCopyToMoveAsm);
    }

    /**
     * buildFuncCallGraph 方法用于在该 Function 中的所有基本块中进行函数调用图构建，
     * 主要用于函数内联
     */
    public void buildFuncCallGraph() {
        basicBlocks.forEach(BasicBlock::buildFuncCallGraph);
    }

    /**
     * dfsCaller 方法用于在该 Function 中的所有基本块中进行函数调用图构建，
     * 是一个深度优先搜索的过程，主要用于函数内联
     */
    public void dfsCaller() {
        FunctionInlineUnit.setInlineAble(true);
        if (!this.getName().equals("@main")) {
            if (!FunctionInlineUnit.getCaller(this).isEmpty() ||
                    FunctionInlineUnit.hasRecursion(this)) {
                FunctionInlineUnit.setInlineAble(false);
            }
            if (FunctionInlineUnit.isInlineAble()) {
                FunctionInlineUnit.addInlineFunction(this);
            }
        }
    }

    /**
     * inlineFunction 方法用于在该 Function 中的所有基本块中进行函数内联，
     * 主要用于函数内联
     * 该函数的执行逻辑如下：
     * 1.通过遍历，获得所有调用 inlineFunc 的 callInstr
     * 2.对于每一个 callInstr，将其替换为 inlineFunc 的所有基本块
     * 3.去除调用关系
     */
    public void inlineFunction() {
        ArrayList<CallInstr> callList = new ArrayList<>();
        if (!FunctionInlineUnit.getResponse(this).isEmpty()) {
            FunctionInlineUnit.setFixedPoint(true);
            for (Function target : FunctionInlineUnit.getResponse(this)) {
                for (BasicBlock basicBlock : target.getBasicBlocks()) {
                    for (Instr instr : basicBlock.getInstrArrayList()) {
                        if (instr instanceof CallInstr callInstr) {
                            if (callInstr.getTarget().equals(this)) {
                                callList.add(callInstr);
                            }
                        }
                    }
                }
            }
        }
        callList.forEach(FunctionInlineUnit::replaceFunctions);
        for (Function target : FunctionInlineUnit.getResponse(this)) {
            FunctionInlineUnit.getCaller(target).remove(this);
            FunctionInlineUnit.getCaller(target).addAll(FunctionInlineUnit.getCaller(this));
        }
        FunctionInlineUnit.getResponse(this).clear();
    }

    /**
     * removeUselessFunction 方法用于在该 Function 中的所有基本块中进行无用函数删除，
     * 主要用于无用函数删除
     */
    public boolean removeUselessFunction() {
        return FunctionInlineUnit.getResponse(this).isEmpty() &&
                !this.getName().equals("@main");
    }

    /**
     * GlobalCodeMovementAnalysis 方法用于在该 Function 中的所有基本块中进行全局代码移动分析。
     * 主要用于全局代码移动
     * 该函数的执行流程如下:
     * 1.首先判断该函数是否只有一个基本块，如果是，直接返回
     * 2.否则，首先计算该函数的支配树后序遍历序列
     * 3.然后，对于每一个基本块，将其指令加入到一个指令列表中
     * 4.对于每一个指令，首先调用 GlobalCodeMovementUnit.scheduleEarly 方法进行向前移动
     * 5.然后，对于每一个指令，调用 GlobalCodeMovementUnit.scheduleLate 方法进行向后移动
     */
    public void globalCodeMovementAnalysis() {
        if (basicBlocks.size() > 1) {
            GlobalCodeMovementUnit.getVisited().clear();
            ArrayList<BasicBlock> posOrderBlocks =
                    DominatorTree.computeDominanceTreePostOrder(this);
            Collections.reverse(posOrderBlocks);
            ArrayList<Instr> instrList = new ArrayList<>();
            posOrderBlocks.forEach(v -> instrList.addAll(v.getInstrArrayList()));
            instrList.forEach(instr ->
                    GlobalCodeMovementUnit.scheduleEarly(instr, this));
            GlobalCodeMovementUnit.getVisited().clear();
            Collections.reverse(instrList);
            instrList.forEach(GlobalCodeMovementUnit::scheduleLate);
        }
    }

    /**
     * searchBlockDominateTreeDepth 方法用于在该 Function 中的所有基本块中进行支配树深度搜索，
     * 主要用于GCM优化
     */
    public void searchBlockDominateTreeDepth() {
        DominatorTree.dfsDominateLevel(basicBlocks.get(0), 0);
    }
}
