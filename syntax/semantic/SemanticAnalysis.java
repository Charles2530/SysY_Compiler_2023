package syntax.semantic;

import syntax.AstNode;

public class SemanticAnalysis {
    private AstNode rootAst;

    private SemanticAnalysisChecker rootChecker;

    public SemanticAnalysis(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void analysis() {
        SemanticAnalysisChecker rootChecker = new SemanticAnalysisChecker(rootAst);
        this.rootChecker = rootChecker;
        preTraverse(rootAst);
    }

    private void preTraverse(AstNode rootAst) {
        for (AstNode astNode : rootAst.getChildList()) {
            preTraverse(astNode);
        }
        rootChecker.checkAnalysis(rootAst);
    }
}
