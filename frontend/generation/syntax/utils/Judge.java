package frontend.generation.syntax.utils;

import frontend.generation.syntax.AstRecursion;

/**
 * Judge 用于判断当前词法所属的语法类型
 */
public class Judge {
    public static boolean isDecl() {
        return isConstDecl() || isVarDecl();
    }

    public static boolean isBtype() {
        return getPreSym().equals("INTTK");
    }

    public static boolean isFuncDef() {
        return isFuncType() && getNextSym(1).equals("IDENFR") && getNextSym(2).equals("LPARENT");
    }

    public static boolean isFuncFParams() {
        return isFuncFParam();
    }

    public static boolean isFuncType() {
        return getPreSym().equals("INTTK") || getPreSym().equals("VOIDTK");
    }

    public static boolean isFuncFParam() {
        return getPreSym().equals("INTTK");
    }

    public static boolean isConstInitVal() {
        return isConstExp() || getPreSym().equals("LBRACE");
    }

    public static boolean isLVal() {
        return isIdent();
    }

    public static boolean isUnaryExp() {
        return isPrimaryExp() || isUnaryOp() || isIdent();
    }

    public static boolean isIdent() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean isUnaryOp() {
        return getPreSym().equals("PLUS") || getPreSym().equals("MINU")
                || getPreSym().equals("NOT");
    }

    public static boolean isPrimaryExp() {
        return getPreSym().equals("LPARENT") || isIdent() || isNumber();
    }

    public static boolean isMulExp() {
        return isUnaryExp();
    }

    public static boolean isAddExp() {
        return isMulExp();
    }

    public static boolean isConstExp() {
        return isAddExp();
    }

    public static boolean isVarDecl() {
        return getPreSym().equals("INTTK") && getNextSym(1).equals("IDENFR")
                && !getNextSym(2).equals("LPARENT");
    }

    public static boolean isVarDef() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean isInitval() {
        return isExp() || getPreSym().equals("LBRACE");
    }

    public static boolean isStmt() {
        return isLVal() || isExp() || isBlock() || isReservedForStmt();
    }

    public static boolean isReservedForStmt() {
        return getPreSym().equals("SEMICN") || getPreSym().equals("IFTK") ||
                getPreSym().equals("FORTK") || getPreSym().equals("BREAKTK") ||
                getPreSym().equals("CONTINUETK") || getPreSym().equals("RETURNTK") ||
                getPreSym().equals("PRINTFTK");
    }

    public static boolean isForStmtVal() {
        return isLVal() || isExp();
    }

    public static boolean isCond() {
        return isUnaryExp();
    }

    public static boolean isBlockItem() {
        return isConstDecl() || isVarDecl() || isStmt();
    }

    public static boolean isBlock() {
        return getPreSym().equals("LBRACE");
    }

    public static boolean isConstDecl() {
        return getPreSym().equals("CONSTTK");
    }

    public static boolean isConstDef() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean isExp() {
        return isAddExp();
    }

    public static boolean isFuncRParams() {
        return isExp();
    }

    public static boolean isNumber() {
        return getPreSym().equals("INTCON");
    }

    /**
     * getPreSym() 和 getNextSym() 用于获取当前词法的类型和pos个位置的词法的类型
     *
     * @return 词法的类型
     */
    public static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }

    public static String getNextSym(int pos) {
        return AstRecursion.getNextSymToken(pos).getReservedWord();
    }

}
