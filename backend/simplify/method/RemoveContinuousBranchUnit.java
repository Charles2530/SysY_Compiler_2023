package backend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.instr.basis.JumpInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;

import java.util.Iterator;

/**
 * RemoveContinuousBranchUnit 是执行RemoveContinuousBranch的单元
 * 主要用于RemoveContinuousBranch
 */
public class RemoveContinuousBranchUnit {
    /**
     * module 是LLVM IR生成的顶级模块
     */
    private static Module module;

    /**
     * run 方法用于执行RemoveContinuousBranch
     */
    public static void run(Module module) {
        RemoveContinuousBranchUnit.module = module;
        RemoveContinuousBranchUnit.removeContinuousBranch();
    }

    /**
     * removeContinuousBranch 方法用于移除连续分支
     */
    private static void removeContinuousBranch() {
        for (Function function : module.getFunctions()) {
            BasicBlock preBasicBlock = null;
            Iterator<BasicBlock> iterator = function.getBasicBlocks().iterator();
            while (iterator.hasNext()) {
                BasicBlock basicBlock = iterator.next();
                if (RemoveContinuousBranchUnit.isMergeAble(preBasicBlock, basicBlock)) {
                    if (basicBlock.getBlockIndBasicBlock().size() == 1) {
                        RemoveContinuousBranchUnit.mergeBlock(preBasicBlock, basicBlock);
                        iterator.remove();
                        continue;
                    } else {
                        if (preBasicBlock.getLastInstr() instanceof JumpInstr jumpInstr) {
                            jumpInstr.setAssemblerReduce();
                        }
                    }
                }
                preBasicBlock = basicBlock;
            }
        }
    }

    /**
     * isMergeAble 方法用于判断两个基本块是否可以合并
     */
    private static boolean isMergeAble(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        return preBasicBlock != null && preBasicBlock.getBlockOutBasicBlock().size() == 1
                && preBasicBlock.getBlockOutBasicBlock().get(0).equals(basicBlock);
    }

    /**
     * mergeBlock 方法用于合并两个基本块
     */
    private static void mergeBlock(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        preBasicBlock.getInstrArrayList().remove(preBasicBlock.getLastInstr());
        preBasicBlock.getInstrArrayList().addAll(basicBlock.getInstrArrayList());
        RemoveContinuousBranchUnit.modifyMerged(preBasicBlock, basicBlock);
    }

    /**
     * modifyMerged 方法用于在合并后修改优化分析的各类参数
     * 参数主要来自于ControlFlowGraph，DominatorTree和LivenessAnalysisController
     */
    private static void modifyMerged(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        ControlFlowGraph.modifyMerged(preBasicBlock, basicBlock);
        DominatorTree.modifyMerged(preBasicBlock, basicBlock);
        LivenessAnalysisController.modifyMerged(preBasicBlock, basicBlock);
    }
}
