package generation.value.construction.procedure;

import generation.value.construction.BasicBlock;

public class Loop {
    private BasicBlock condBlock;
    private BasicBlock currentLoopBlock;
    private BasicBlock followBlock;

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
