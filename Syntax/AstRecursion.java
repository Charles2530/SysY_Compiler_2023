package Syntax;

import Lexer.SymToken;
import Syntax.Decl.Decl;
import Syntax.FuncDef.FuncDef;
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

    public void CompUnit() {
        while (IsDecl()) {
            new Decl(rootAst);
        }
        while (IsFuncDef()) {
            new FuncDef(rootAst);
        }
        new MainFuncDef(rootAst);
    }

    /*TODO: 这里之后过了样例后需要修改*/
    private boolean IsDecl() {
        //        return Definer.IsConstDecl() || Definer.IsVarDecl();
        return false;
    }

    private boolean IsFuncDef() {
        return false;
    }
}
