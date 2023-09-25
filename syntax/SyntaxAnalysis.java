package syntax;

import lexer.SymToken;

import java.io.BufferedWriter;
import java.util.ArrayList;

public class SyntaxAnalysis {
    private final ArrayList<SymToken> symTokens;
    private static boolean isDebugMode;
    private final boolean isParserOutput;

    private final BufferedWriter parserOutputStream;

    private AstNode RootAst = new AstNode("<CompUnit>");

    public SyntaxAnalysis(BufferedWriter parserOutputStream
            , ArrayList<SymToken> symTokens, boolean isDebugMode, boolean isParserOutput) {
        this.symTokens = symTokens;
        SyntaxAnalysis.isDebugMode = isDebugMode;
        this.parserOutputStream = parserOutputStream;
        this.isParserOutput = isParserOutput;
    }

    public void analysis() {
        AstRecursion astRecursion = new AstRecursion(symTokens, RootAst);
        astRecursion.CompUnit();
        //后续遍历AST树
        preTraverse(RootAst);
    }

    private void preTraverse(AstNode rootAst) {
        for (AstNode astNode : rootAst.getChildList()) {
            preTraverse(astNode);
        }
        if (isParserOutput) {
            if (rootAst.isLeaf()) {
                try {
                    parserOutputStream.write(rootAst.getSymToken().getReservedWord()
                            + " " + rootAst.getSymToken().getWord());
                    parserOutputStream.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isDebugMode) {
                        System.err.println(rootAst.getGrammarType());
                    }
                }
            } else {
                if (!(rootAst.getGrammarType().equals("<BlockItem>") || rootAst.getGrammarType()
                        .equals("<BType>") || rootAst.getGrammarType().equals("<Decl>"))) {
                    try {
                        parserOutputStream.write(rootAst.getGrammarType());
                        parserOutputStream.newLine();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public AstNode getAst() {
        return RootAst;
    }

    public static boolean getDebugMode() {
        return isDebugMode;
    }
}