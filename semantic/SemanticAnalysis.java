package semantic;

import iostream.ErrorController;
import semantic.symtable.SymbolTable;
import semantic.utils.SymChecker;
import syntax.AstNode;

import java.io.IOException;

public class SemanticAnalysis {
    private final AstNode rootAst;
    private static SymChecker rootChecker;

    public SemanticAnalysis(AstNode rootAst) {
        this.rootAst = rootAst;
        SymbolTable.init();
    }

    public static SymChecker getRootChecker() {
        return rootChecker;
    }

    public void analysis() throws IOException {
        SemanticAnalysis.rootChecker = new SymChecker();
        SemanticAnalysis.rootChecker.check(rootAst);
        ErrorController.printErrors();
    }

    public static void preTraverse(AstNode rootAst) throws IOException {
        if (rootAst.isLeaf()) {
            return;
        }
        for (AstNode astNode : rootAst.getChildList()) {
            SemanticAnalysis.rootChecker.check(astNode);
            if (astNode.getGrammarType().equals("FORTK")) {
                break;
            }
        }
    }
}
