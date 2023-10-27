package midend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.JumpInstr;

import java.util.ArrayList;
import java.util.HashSet;

public class BlockSimplifyUnit {
    public static void run(Module module) {
        module.simplifyBlock();
    }

    private static void dfs(BasicBlock basicBlock, HashSet<BasicBlock> vis) {
        vis.add(basicBlock);
        Instr lastInstr = basicBlock.getLastInstr();
        if (lastInstr instanceof BrInstr branchInstr) {
            if (branchInstr.getThenBlock() != null && !vis.contains(branchInstr.getThenBlock())) {
                dfs(branchInstr.getThenBlock(), vis);
            }
            if (branchInstr.getElseBlock() != null && !vis.contains(branchInstr.getElseBlock())) {
                dfs(branchInstr.getElseBlock(), vis);
            }
        } else if (lastInstr instanceof JumpInstr jumpInstr) {
            if (jumpInstr.getTarget() != null && !vis.contains(jumpInstr.getTarget())) {
                dfs(jumpInstr.getTarget(), vis);
            }
        }
    }

    public static void deleteDuplicateBranch(BasicBlock basicBlock) {
        for (int i = 0; i < basicBlock.getInstrArrayList().size() - 1; i++) {
            Instr instr = basicBlock.getInstrArrayList().get(i);
            if (instr instanceof BrInstr || instr instanceof JumpInstr) {
                basicBlock.setInstrArrayList(new ArrayList<>(
                        basicBlock.getInstrArrayList().subList(0, i + 1)));
                break;
            }
        }
    }

    public static void deleteDeadBlock(Function function) {
        HashSet<BasicBlock> vis = new HashSet<>();
        BasicBlock entry = function.getBasicBlocks().get(0);
        dfs(entry, vis);
        function.getBasicBlocks().removeIf(bb -> !vis.contains(bb) && bb.setDeleted(true));
    }
}
