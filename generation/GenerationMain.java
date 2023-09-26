package generation;

import syntax.AstNode;

public class GenerationMain {
    private Boolean isDebugMode;
    private AstNode RootAst;

    public GenerationMain(AstNode RootAst, boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
        this.RootAst = RootAst;
    }

    public void generate() {

    }
}
