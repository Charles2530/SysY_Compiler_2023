package frontend.generation.syntax;

import frontend.generation.lexer.SymToken;
import frontend.generation.syntax.decl.Decl;
import frontend.generation.syntax.funcdef.FuncDef;
import frontend.generation.syntax.utils.Judge;
import frontend.generation.syntax.mainfuncdef.MainFuncDef;

import java.io.IOException;
import java.util.ArrayList;

public class AstRecursion {
    /**
     * AstRecursion 是执行递归下降算法的核心类
     * symTokens 为之前词法分析获得的结果
     * symPos 为当前读取的单词在symTokens中的位置
     * preSymToken 为当前读取的单词
     * previousNoTerminalAst 为上一个非终结符节点
     */
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

    /**
     * nextSym() 用于读取下一个单词,并将preSymToken指向当前单词
     */
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

    /**
     * getNextSymToken() 用于预读pos个位置的单词，解决FIRST集合中存在交集的情况
     */
    public static SymToken getNextSymToken(int pos) {
        if (symPos + pos > symTokens.size() - 1 || symPos + pos < 0) {
            return new SymToken("EOF", "EOF", -1);
        }
        return symTokens.get(symPos + pos);
    }

    /**
     * 从CompUnit开始，递归下降算法的入口
     */
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
