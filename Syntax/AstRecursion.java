package Syntax;

import Lexer.SymToken;
import Syntax.Decl.Decl;
import Syntax.FuncDef.FuncDef;
import Syntax.Handler.Judge;
import Syntax.MainFuncDef.MainFuncDef;

import java.util.ArrayList;

public class AstRecursion {
    private static ArrayList<SymToken> symTokens;
    private static int symPos;
    private static SymToken preSymToken;
    private AstNode rootAst;

    public AstRecursion(ArrayList<SymToken> symTokens, AstNode rootAst) {
        AstRecursion.symTokens = symTokens;
        symPos = 0;
        preSymToken = symTokens.get(symPos);
        this.rootAst = rootAst;
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
        if (symPos + pos > symTokens.size() - 1) {
            return null;
        }
        return symTokens.get(symPos + pos);
    }

    public void CompUnit() {
        while (Judge.IsDecl()) {
            new Decl(rootAst);
        }
        while (Judge.IsFuncDef()) {
            new FuncDef(rootAst);
        }
        new MainFuncDef(rootAst);
    }
}
