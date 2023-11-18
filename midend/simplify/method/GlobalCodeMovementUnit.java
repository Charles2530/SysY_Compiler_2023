package midend.simplify.method;

import iostream.OptimizerUnit;
import iostream.structure.DebugDetailController;
import midend.generation.GenerationMain;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.User;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.optimizer.PhiInstr;
import midend.simplify.controller.LoopAnalysisController;
import midend.simplify.controller.SideEffectAnalysisController;
import midend.simplify.controller.datastruct.DominatorTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * GlobalCodeMovementUnit 是全局代码移动单元，
 * 主要用于全局代码移动
 * 一个指令的位置是由他使用的指令和使用他的指令决定的
 * 我们找到的是一个区间，这个区间上指令可以自由的移动
 * 我们要挑选尽可能靠近支配树根节点和尽可能循环深度比较深的点
 */
public class GlobalCodeMovementUnit {
    /**
     * visited 是该 GlobalCodeMovementUnit 的已访问指令集合
     * pathMap 是该 GlobalCodeMovementUnit 的路径映射,记录了指令的移动路径
     * 主要用于调试
     */
    private static HashSet<Instr> visited;
    private static HashMap<Instr, ArrayList<BasicBlock>> pathMap;

    public static void run(Module module) {
        GlobalCodeMovementUnit.init();
        DominatorTree.build(module);
        LoopAnalysisController.analysis(module);
        SideEffectAnalysisController.analysis(module);
        module.getFunctions().forEach(Function::globalCodeMovementAnalysis);
        DebugDetailController.printGlobalCodeMovementPath(pathMap);
        OptimizerUnit.build(module);
    }

    /**
     * init 方法用于初始化该 GlobalCodeMovementUnit
     */
    private static void init() {
        GlobalCodeMovementUnit.visited = new HashSet<>();
        GlobalCodeMovementUnit.pathMap = new HashMap<>();
        for (Function function : GenerationMain.getModule().getFunctions()) {
            function.getBasicBlocks().forEach(basicBlock -> {
                basicBlock.getInstrArrayList().forEach(instr -> {
                    GlobalCodeMovementUnit.pathMap.put(instr, new ArrayList<>());
                    GlobalCodeMovementUnit.pathMap.get(instr).add(basicBlock);
                });
            });
        }
    }

    /**
     * addPath 方法用于向该 GlobalCodeMovementUnit 的路径映射中添加路径
     */
    public static void addPath(Instr instr, BasicBlock basicBlock) {
        GlobalCodeMovementUnit.pathMap.get(instr).add(basicBlock);
    }

    public static HashSet<Instr> getVisited() {
        return visited;
    }

    /**
     * scheduleEarly 用于在GCM中实现尽可能的把指令前移，
     * 确定每个指令能被调度到的最早的基本块，同时不影响指令间的依赖关系。
     * 当我们把指令向前提时，限制它前移的是它的输入，
     * 即每条指令最早要在它的所有输入定义后的位置。
     * 该函数执行逻辑如下:
     * 1.如果已经处理过了，或者是无法移动，那么就结束处理。
     * 2.如果未处理，将这条指令从当前块移除，然后插入到入口块的最后一条指令之前。
     * 3.遍历该指令用到的操作数，尝试前移。
     */
    public static void scheduleEarly(Instr instr, Function function) {
        if (visited.contains(instr) || instr.isPinned()) {
            return;
        }
        visited.add(instr);
        BasicBlock root = function.getBasicBlocks().get(0);
        instr.getBelongingBlock().getInstrArrayList().remove(instr);
        root.addInstr(instr, root.getInstrArrayList().size() - 1);
        GlobalCodeMovementUnit.addPath(instr, root);
        instr.getOperands().forEach(v -> scheduleEarlyAnalysis(v, instr, function));
    }

    /**
     * scheduleLate 用于在GCM中尽可能的把指令后移，
     * 确定每个指令能被调度到的最晚的基本块。
     * 每个指令也会被使用它们的指令限制，限制其不能无限向后移。
     * 该函数执行逻辑如下:
     * 1.如果已经处理过了，或者是无法移动，那么就结束处理。
     * 2.如果未处理，遍历该指令的使用者，寻找LCA尝试后移。
     * 3.如果该指令的使用者是Phi指令，那么遍历Phi指令的每个操作数，寻找LCA尝试后移。
     */
    public static void scheduleLate(Instr instr) {
        if (visited.contains(instr) || instr.isPinned()) {
            return;
        }
        visited.add(instr);
        BasicBlock lcaBlock = null;
        for (User user : instr.getUsers()) {
            lcaBlock = scheduleLateAnalysis(user, instr, lcaBlock);
        }
        GlobalCodeMovementUnit.pickFinalPos(lcaBlock, instr);
        BasicBlock bestBlock = instr.getBelongingBlock();
        for (Instr instInst : bestBlock.getInstrArrayList()) {
            if (!instInst.equals(instr) && !(instInst instanceof PhiInstr) &&
                    instInst.getOperands().contains(instr)) {
                instr.getBelongingBlock().getInstrArrayList().remove(instr);
                bestBlock.addInstr(instr, bestBlock.getInstrArrayList().indexOf(instInst));
                GlobalCodeMovementUnit.addPath(instr, bestBlock);
                break;
            }
        }
    }

    /**
     * pickFinalPos 用于在GCM中寻找指令最终所处的位置
     * 该函数的执行逻辑如下：
     * 1.如果该指令没有使用者，那么直接返回
     * 2.如果该指令有使用者，那么遍历该指令的使用者，找到最佳位置
     * 3.找到他们的LCA，如果他们是有共同祖先的，那么就将该指令插入到共同祖先的位置
     * 4.如果共同祖先不是该指令的所在块，则尽量让循环深度变小
     */
    private static void pickFinalPos(BasicBlock lcaBlock, Instr instr) {
        BasicBlock posBlock = lcaBlock;
        if (!instr.getUsers().isEmpty()) {
            BasicBlock bestBlock = posBlock;
            while ((posBlock.getBlockDominateParent() != null) &&
                    !posBlock.equals(instr.getBelongingBlock())) {
                posBlock = posBlock.getBlockDominateParent();
                if (posBlock.getLoopDepth() < bestBlock.getLoopDepth()) {
                    bestBlock = posBlock;
                }
            }
            instr.getBelongingBlock().getInstrArrayList().remove(instr);
            bestBlock.addInstr(instr, bestBlock.getInstrArrayList().size() - 1);
            GlobalCodeMovementUnit.addPath(instr, bestBlock);
        }
    }

    /**
     * scheduleLateAnalysis() 用于在优化中调度该Value的后移分析
     * 该函数的基本逻辑是寻找某个Value所对应User的公共LCA，之后
     * 就可以将指令移动到LCA所在的基本块
     *
     * @param user     使用该Value的指令
     * @param instr    该Value
     * @param lcaBlock 该Value的最近公共祖先
     */
    private static BasicBlock scheduleLateAnalysis(User user, Instr instr, BasicBlock lcaBlock) {
        BasicBlock lcaVector = lcaBlock;
        if (user instanceof Instr userInst) {
            GlobalCodeMovementUnit.scheduleLate(userInst);
            BasicBlock useBasicBlock;
            if (userInst instanceof PhiInstr phiInstr) {
                for (int i = 0; i < phiInstr.getOperands().size(); i++) {
                    if (phiInstr.getOperands().get(i) instanceof Instr instInst &&
                            instr.equals(instInst)) {
                        useBasicBlock = phiInstr.getIndBasicBlock().get(i);
                        lcaVector = getLeastCommonAncestor(lcaVector, useBasicBlock);
                    }
                }
            } else {
                useBasicBlock = userInst.getBelongingBlock();
                lcaVector = getLeastCommonAncestor(lcaVector, useBasicBlock);
            }
        }
        return lcaVector;
    }

    /**
     * getLeastCommonAncestor 用于在GCM中寻找两个基本块的最近公共祖先
     */
    private static BasicBlock getLeastCommonAncestor(
            BasicBlock lcaBlock, BasicBlock useBasicBlock) {
        BasicBlock block1 = lcaBlock;
        BasicBlock block2 = useBasicBlock;
        if (block1 == null) {
            return block2;
        }
        while (block1.getDomDepth() < block2.getDomDepth()) {
            block2 = block2.getBlockDominateParent();
        }
        while (block1.getDomDepth() > block2.getDomDepth()) {
            block1 = block1.getBlockDominateParent();
        }
        while (!(block1.equals(block2))) {
            block1 = block1.getBlockDominateParent();
            block2 = block2.getBlockDominateParent();
        }
        return block1;
    }

    /**
     * scheduleEarlyAnalysis() 用于在优化中调度该Value的前移分析
     * 主要用于GCM优化
     * 该函数执行逻辑如下:
     * 1.如果该操作数是个指令，那么应该是一并提升
     * 2.比较两个指令和操作数的支配树深度，如果指令的深度要小于操作数的深度
     * 则将指令插在输入指令的基本块的最后一条指令的前面
     * 注：这里若为PhiInstr，则操作数不需要考虑indBasicBlocks，因为其中不含instr
     */
    public static void scheduleEarlyAnalysis(Value value, Instr instr, Function function) {
        if (value instanceof Instr instrInst) {
            GlobalCodeMovementUnit.scheduleEarly(instrInst, function);
            if (instrInst.getBelongingBlock().getDomDepth() >
                    instr.getBelongingBlock().getDomDepth()) {
                instr.getBelongingBlock().getInstrArrayList().remove(instr);
                BasicBlock block = instrInst.getBelongingBlock();
                block.addInstr(instr, block.getInstrArrayList().size() - 1);
                GlobalCodeMovementUnit.addPath(instr, block);
            }
        }
    }
}
