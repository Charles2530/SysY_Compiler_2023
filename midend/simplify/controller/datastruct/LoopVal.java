package midend.simplify.controller.datastruct;

import midend.generation.value.construction.BasicBlock;
import midend.simplify.controller.LoopAnalysisController;

import java.util.ArrayList;

/**
 * LoopVal 是循环的数据结构
 * 用于循环分析
 */
public class LoopVal {
    /**
     * entryBlock 循环头块，位于循环内部，一个循环有且仅有一个头块，
     * 一个头块可以被多个循环拥有。每次循环首先执行头块，一定会执行
     * 头块支配循环中所有基本块，但是被头块支配的基本块不一定在循环中
     * basicBlocks 循环包含的所有基本块，包括子循环的基本块
     * childLoopList 直接子循环，不包括子循环的子循环
     * latchBlocks 闩锁块，位于循环中，其中有一个后继是头块
     * parentLoop 最外层循环parentLoop为 null
     * loopDepth 最外层循环深度为 1
     */
    private final BasicBlock entryBlock;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private final ArrayList<LoopVal> childLoopList = new ArrayList<>();
    private final ArrayList<BasicBlock> latchBlocks = new ArrayList<>();
    private LoopVal parentLoop;
    private int loopDepth;

    /**
     * LoopVal 新建一个循环
     *
     * @param entryBlock  入口块
     * @param latchBlocks 栓块
     */
    public LoopVal(BasicBlock entryBlock, ArrayList<BasicBlock> latchBlocks) {
        this.entryBlock = entryBlock;
        entryBlock.setLoopVal(this);
        this.latchBlocks.addAll(latchBlocks);
    }

    public void addSubLoop(LoopVal loop) {
        if (!childLoopList.contains(loop)) {
            childLoopList.add(loop);
        }
    }

    public void addBlock(BasicBlock block) {
        if (!basicBlocks.contains(block)) {
            basicBlocks.add(block);
            LoopAnalysisController.addBlockLoopDepth(block, block.getLoopDepth());
        }
    }

    public void setLoopDepth(int loopDepth) {
        this.loopDepth = loopDepth;
    }

    public int getLoopDepth() {
        return loopDepth;
    }

    public ArrayList<BasicBlock> getLoopBlocks() {
        return basicBlocks;
    }

    public LoopVal getParentLoop() {
        return parentLoop;
    }

    public BasicBlock getEntryBlock() {
        return entryBlock;
    }

    public void setParentLoop(LoopVal parentLoop) {
        this.parentLoop = parentLoop;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("entryBlock:");
        for (BasicBlock block : basicBlocks) {
            sb.append(block.getName()).append("\n").append("\t\t\t\t\t\t");
        }
        return sb.toString();
    }
}
