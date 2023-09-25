package llvm;

import syntax.AstNode;

public class LLvmMain {
    private Boolean isDebugMode;
    private AstNode RootAst;

    public LLvmMain(AstNode RootAst, boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
        this.RootAst = RootAst;
    }

    public void generate() {

    }
}
