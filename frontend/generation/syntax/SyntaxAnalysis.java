package frontend.generation.syntax;

import iostream.ErrorController;
import iostream.OutputController;
import frontend.generation.lexer.SymToken;

import java.io.IOException;
import java.util.ArrayList;

public class SyntaxAnalysis {
    /**
     * symTokens 为之前词法分析获得的结果
     * rootAst 为语法分析AST树的根节点
     * */
    private final ArrayList<SymToken> symTokens;

    private AstNode rootAst;

    public SyntaxAnalysis(ArrayList<SymToken> symTokens) {
        this.symTokens = symTokens;
    }

    /**
     * 语法分析主程序
     * */
    public void analysis() throws IOException {
        AstRecursion astRecursion = new AstRecursion(symTokens);
        this.rootAst = new AstNode("<CompUnit>");
        astRecursion.genCompUnit(rootAst);
        preTraverse(rootAst);
    }

    /**
     * 通过后续遍历AST树，输出语法分析结果
     * */
    private void preTraverse(AstNode rootAst) throws IOException {
        for (AstNode astNode : rootAst.getChildList()) {
            preTraverse(astNode);
        }
        if (rootAst.isLeaf()) {
            try {
                OutputController.syntaxAnalysisPrintTerminal(rootAst);
            } catch (Exception e) {
                ErrorController.printSyntaxAnalysisError(rootAst.getGrammarType());
                e.printStackTrace();
            }
        } else {
            if (!(rootAst.getGrammarType().equals("<BlockItem>") || rootAst.getGrammarType()
                    .equals("<BType>") || rootAst.getGrammarType().equals("<Decl>"))) {
                try {
                    OutputController.syntaxAnalysisPrintNoTerminal(rootAst);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public AstNode getAst() {
        return rootAst;
    }
}