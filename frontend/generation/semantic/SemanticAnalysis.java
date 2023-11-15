package frontend.generation.semantic;

import iostream.structure.ErrorController;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.utils.SymChecker;
import frontend.generation.syntax.AstNode;

import java.io.IOException;

/**
 * SemanticAnalysis 是语义分析主程序
 */
public class SemanticAnalysis {
    /**
     * rootAst 是语法分析树的根节点
     * rootChecker 是语义分析用于检查符号表表项是否存在语义错误或语法错误的检查器
     */
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

    /**
     * preTraverse() 用于对语法分析树进行先序遍历,并进行语义分析创建符号表
     */
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
