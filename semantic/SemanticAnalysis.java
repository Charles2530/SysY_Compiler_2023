package semantic;

import generation.utils.ErrorController;
import semantic.utils.symChecker;
import syntax.AstNode;

import java.io.IOException;

public class SemanticAnalysis {
    private AstNode rootAst;
    private static symChecker rootChecker;

    public SemanticAnalysis(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void analysis() throws IOException {
        SemanticAnalysis.rootChecker = new symChecker();
        SemanticAnalysis.rootChecker.check(rootAst);
        ErrorController.printErrors();
    }

    public static void preTraverse(AstNode rootAst) throws IOException {
        if (rootAst.isLeaf()) {
            return;
        }
        for (AstNode astNode : rootAst.getChildList()) {
            SemanticAnalysis.rootChecker.check(astNode);
        }
    }
}
