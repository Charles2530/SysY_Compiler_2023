package generation;

import syntax.AstNode;

public class GenerationMain {
    private Boolean isDebugMode;
    private AstNode rootAst;

    public GenerationMain(AstNode rootAst, boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
        this.rootAst = rootAst;
    }

    public void generate() {

    }
}
