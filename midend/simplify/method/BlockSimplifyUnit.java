package midend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.JumpInstr;
import midend.generation.value.instr.basis.RetInstr;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * BlockSimplifyUnit 是基本块简化单元，
 * 主要用于基本块的简化
 */
public class BlockSimplifyUnit {
    /**
     * dfs 是该 BlockSimplifyUnit 的深度优先搜索函数
     * 该函数用于深度优先搜索基本块
     *
     * @param basicBlock 是该基本块
     *                   该基本块将被深度优先搜索
     * @param vis        是该基本块的访问哈希表
     *                   该哈希表用于记录该基本块是否被访问过
     */
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

    /**
     * deleteDuplicateBranch 方法用于删除重复的分支
     * 该方法用于删除基本块中重复的分支，对于每个基本块
     * 而言，仅需保留第一个分支即可，该分支以下的代码不
     * 会被执行
     */
    public static void deleteDuplicateBranch(BasicBlock basicBlock) {
        for (int i = 0; i < basicBlock.getInstrArrayList().size() - 1; i++) {
            Instr instr = basicBlock.getInstrArrayList().get(i);
            if (instr instanceof BrInstr || instr instanceof JumpInstr
                    || instr instanceof RetInstr) {
                basicBlock.setInstrArrayList(new ArrayList<>(
                        basicBlock.getInstrArrayList().subList(0, i + 1)));
                break;
            }
        }
    }

    /**
     * deleteDeadBlock 方法用于删除死代码块
     * 该方法用于删除函数中的死代码块，对于每个基本块
     * 而言，如果该基本块不可达(即不在vis中)，则该基本块
     * 为死代码块，应当被删除
     */
    public static void deleteDeadBlock(Function function) {
        HashSet<BasicBlock> vis = new HashSet<>();
        BasicBlock entry = function.getBasicBlocks().get(0);
        dfs(entry, vis);
        function.getBasicBlocks().removeIf(bb -> !vis.contains(bb) && bb.setDeleted(true));
    }
}
