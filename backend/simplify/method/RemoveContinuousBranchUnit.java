package backend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.instr.basis.JumpInstr;
import midend.simplify.controller.LivenessAnalysisController;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;

import java.util.Iterator;

public class RemoveContinuousBranchUnit {
    private static Module module;

    public static void run(Module module) {
        RemoveContinuousBranchUnit.module = module;
        RemoveContinuousBranchUnit.removeContinuousBranch();
    }

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

    private static boolean isMergeAble(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        return preBasicBlock != null && preBasicBlock.getBlockOutBasicBlock().size() == 1
                && preBasicBlock.getBlockOutBasicBlock().get(0).equals(basicBlock);
    }

    private static void mergeBlock(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        preBasicBlock.getInstrArrayList().remove(preBasicBlock.getLastInstr());
        preBasicBlock.getInstrArrayList().addAll(basicBlock.getInstrArrayList());
        RemoveContinuousBranchUnit.modifyMerged(preBasicBlock, basicBlock);
    }

    private static void modifyMerged(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        ControlFlowGraph.modifyMerged(preBasicBlock, basicBlock);
        DominatorTree.modifyMerged(preBasicBlock, basicBlock);
        LivenessAnalysisController.modifyMerged(preBasicBlock, basicBlock);
    }
}
