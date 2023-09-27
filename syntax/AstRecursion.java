package syntax;

import lexer.SymToken;
import syntax.decl.Decl;
import syntax.funcDef.FuncDef;
import syntax.utils.Judge;
import syntax.mainFuncDef.MainFuncDef;

import java.io.IOException;
import java.util.ArrayList;

public class AstRecursion {
    private static ArrayList<SymToken> symTokens;
    private static int symPos;
    private static SymToken preSymToken;
    private AstNode rootAst;
    private static AstNode previousNoTerminalAst;

    public static void setPreviousNoTerminalAst(AstNode previousNoTerminalAst) {
        AstRecursion.previousNoTerminalAst = previousNoTerminalAst;
    }

    public static AstNode getPreviousNoTerminalAst() {
        return previousNoTerminalAst;
    }

    public AstRecursion(ArrayList<SymToken> symTokens) {
        AstRecursion.symTokens = symTokens;
        AstRecursion.symPos = 0;
        AstRecursion.preSymToken = symTokens.get(symPos);
    }

    public static void nextSym() {
        if (symPos == symTokens.size() - 1) {
            return;
        }
        symPos++;
        preSymToken = symTokens.get(symPos);
    }

    public static SymToken getPreSymToken() {
        return preSymToken;
    }

    public static SymToken getNextSymToken(int pos) {
        if (symPos + pos > symTokens.size() - 1 || symPos + pos < 0) {
            return null;
        }
        return symTokens.get(symPos + pos);
    }

    public void CompUnit(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        while (Judge.IsDecl()) {
            new Decl(rootAst);
        }
        while (Judge.IsFuncDef()) {
            new FuncDef(rootAst);
        }
        new MainFuncDef(rootAst);
    }
}
