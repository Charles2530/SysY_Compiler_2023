package frontend.generation.syntax;

import iostream.ErrorController;
import iostream.OutputController;
import frontend.generation.lexer.SymToken;

import java.io.IOException;
import java.util.ArrayList;

public class SyntaxAnalysis {
    private final ArrayList<SymToken> symTokens;

    private AstNode rootAst;

    public SyntaxAnalysis(ArrayList<SymToken> symTokens) {
        this.symTokens = symTokens;
    }

    public void analysis() throws IOException {
        AstRecursion astRecursion = new AstRecursion(symTokens);
        this.rootAst = new AstNode("<CompUnit>");
        astRecursion.genCompUnit(rootAst);
        preTraverse(rootAst);
    }

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