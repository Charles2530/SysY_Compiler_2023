package Syntax.Handler;

import Syntax.AstRecursion;

public class Judge {
    public static boolean IsLVal() {
        return IsIdent() && !getNextSym().equals("LPARENT");
    }

    public static boolean IsUnaryExp() {
        return IsPrimaryExp() || IsUnaryOp() || IsIdent();
    }

    public static boolean IsIdent() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean IsUnaryOp() {
        return getPreSym().equals("PLUS") || getPreSym().equals("MINU")
                || getPreSym().equals("NOT");
    }

    public static boolean IsPrimaryExp() {
        return getPreSym().equals("LPARENT") || IsLVal() || IsNumber();
    }

    public static boolean IsMulExp() {
        return IsUnaryExp();
    }

    public static boolean IsAddExp() {
        return IsMulExp();
    }

    public static boolean IsConstExp() {
        return IsAddExp();
    }

    public static boolean IsVarDecl() {
        if (getPreSym().equals("INTTK")) {
            return true;
        }
        return false;
    }

    public static boolean IsVarDef() {
        if (getPreSym().equals("IDENFR")) {
            return true;
        }
        return false;
    }

    public static boolean IsInitval() {
        return IsExp() || getPreSym().equals("LBRACE");
    }

    public static boolean IsStmt() {
        return IsLVal() || IsExp() || IsBlock() || IsReservedForStmt();
    }

    public static boolean IsReservedForStmt() {
        return getPreSym().equals("SEMICN") || getPreSym().equals("IFTK") ||
                getPreSym().equals("FORTK") || getPreSym().equals("BREAKTK") ||
                getPreSym().equals("CONTINUETK") || getPreSym().equals("RETURNTK") ||
                getPreSym().equals("PRINTFTK");
    }

    public static boolean IsForStmtVal() {
        return IsLVal() || IsExp();
    }

    public static boolean IsCond() {
        return IsUnaryExp();
    }

    public static boolean IsBlockItem() {
        return IsConstDecl() || IsVarDecl() || IsStmt();
    }

    public static boolean IsBlock() {
        return getPreSym().equals("LBRACE");
    }

    public static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }

    public static String getNextSym() {
        return AstRecursion.getNextSymToken().getReservedWord();
    }

    public static boolean IsConstDecl() {
        return getPreSym().equals("CONSTTK");
    }

    public static boolean IsConstDef() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean IsExp() {
        return IsAddExp();
    }

    public static boolean IsFuncRParams() {
        return IsExp();
    }

    public static boolean IsNumber() {
        return getPreSym().equals("INTCON");
    }
}
