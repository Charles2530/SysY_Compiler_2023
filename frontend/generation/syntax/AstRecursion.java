package frontend.generation.syntax;

import frontend.generation.lexer.SymToken;
import frontend.generation.syntax.decl.Decl;
import frontend.generation.syntax.funcdef.FuncDef;
import frontend.generation.syntax.utils.Judge;
import frontend.generation.syntax.mainfuncdef.MainFuncDef;

import java.io.IOException;
import java.util.ArrayList;

public class AstRecursion {
    private static ArrayList<SymToken> symTokens;
    private static int symPos;
    private static SymToken preSymToken;
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
            return new SymToken("EOF", "EOF", -1);
        }
        return symTokens.get(symPos + pos);
    }

    public void genCompUnit(AstNode rootAst) throws IOException {
        while (Judge.isDecl()) {
            new Decl(rootAst);
        }
        while (Judge.isFuncDef()) {
            new FuncDef(rootAst);
        }
        new MainFuncDef(rootAst);
    }
}