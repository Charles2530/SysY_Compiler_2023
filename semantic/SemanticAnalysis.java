package semantic;

import semantic.symbolTable.SymbolTable;
import semantic.utils.symChecker;
import syntax.AstNode;

public class SemanticAnalysis {
    private AstNode rootAst;
    private static symChecker rootChecker;
    private SymbolTable symbolTable;

    public SemanticAnalysis(AstNode rootAst) {
        this.rootAst = rootAst;
        this.symbolTable = new SymbolTable();
    }

    public void analysis() {
        SemanticAnalysis.rootChecker = new symChecker(rootAst, symbolTable);
        preTraverse(rootAst);
    }

    public static void preTraverse(AstNode rootAst) {
        if (rootAst.isLeaf()) {
            return;
        }
        for (AstNode astNode : rootAst.getChildList()) {
            rootChecker.check(astNode);
        }
    }
}
