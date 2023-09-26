package semantic;

import syntax.AstNode;

public class SemanticAnalysis {
    private AstNode rootAst;
    private static boolean isDebugMode;

    public SemanticAnalysis(AstNode rootAst, boolean isDebugMode) {
        this.rootAst = rootAst;
        SemanticAnalysis.isDebugMode = isDebugMode;
    }

    public void analysis() {
    }
}
