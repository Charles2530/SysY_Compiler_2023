package midend.generation.value.construction.procedure;

import midend.generation.value.construction.BasicBlock;

public class Loop {
    private final BasicBlock condBlock;
    private final BasicBlock currentLoopBlock;
    private final BasicBlock followBlock;

    public Loop(BasicBlock condBlock, BasicBlock loopBodyBlock, BasicBlock followBlock) {
        this.condBlock = condBlock;
        this.currentLoopBlock = loopBodyBlock;
        this.followBlock = followBlock;
    }

    public BasicBlock getCondBlock() {
        return condBlock;
    }

    public BasicBlock getCurrentLoopBlock() {
        return currentLoopBlock;
    }

    public BasicBlock getFollowBlock() {
        return followBlock;
    }
}
