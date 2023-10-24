package midend.generation.value.construction.procedure;

import frontend.syntax.AstNode;
import midend.generation.value.construction.BasicBlock;

public class Loop {
    private final AstNode forStmtVal1;
    private final BasicBlock condBlock;
    private final AstNode forStmtVal2;
    private final BasicBlock currentLoopBlock;
    private final BasicBlock followBlock;

    public Loop(AstNode forStmtVal1, BasicBlock condBlock, AstNode forStmtVal2,
                BasicBlock loopBodyBlock, BasicBlock followBlock) {
        this.forStmtVal1 = forStmtVal1;
        this.condBlock = condBlock;
        this.forStmtVal2 = forStmtVal2;
        this.currentLoopBlock = loopBodyBlock;
        this.followBlock = followBlock;
    }

    public AstNode getForStmtVal1() {
        return forStmtVal1;
    }

    public BasicBlock getCondBlock() {
        return condBlock;
    }

    public AstNode getForStmtVal2() {
        return forStmtVal2;
    }

    public BasicBlock getCurrentLoopBlock() {
        return currentLoopBlock;
    }

    public BasicBlock getFollowBlock() {
        return followBlock;
    }

}
