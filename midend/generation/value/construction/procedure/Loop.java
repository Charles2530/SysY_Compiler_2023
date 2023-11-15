package midend.generation.value.construction.procedure;

import frontend.generation.syntax.AstNode;
import midend.generation.value.construction.BasicBlock;

/**
 * Loop 是 LLVM IR 中的循环成分，
 * 主要用于生成循环
 * 在本学期编译文法中对应for循环
 */
public class Loop {
    /**
     * forStmtVal1 是该 for循环 的第一个语句
     * condBlock 是该 for循环 的条件块
     * forStmtVal2 是该 for循环 的第三个语句
     * currentLoopBlock 是该 Loop 的循环体块
     * followBlock 是该 Loop 的后继块
     */
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
