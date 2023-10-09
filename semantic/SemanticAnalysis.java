package semantic;

import generation.utils.ErrorController;
import semantic.symbolTable.SymbolTable;
import semantic.utils.symChecker;
import syntax.AstNode;

import java.io.IOException;

public class SemanticAnalysis {
    private AstNode rootAst;
    private static symChecker rootChecker;
    private SymbolTable symbolTable;

    public SemanticAnalysis(AstNode rootAst) {
        this.rootAst = rootAst;
        this.symbolTable = new SymbolTable();
    }

    public void analysis() throws IOException {
        SemanticAnalysis.rootChecker = new symChecker(symbolTable);
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
