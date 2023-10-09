package syntax.utils;

import syntax.AstRecursion;

public class Judge {
    public static boolean IsDecl() {
        return IsConstDecl() || IsVarDecl();
    }

    public static boolean IsFuncDef() {
        return IsFuncType() && getNextSym(1).equals("IDENFR") && getNextSym(2).equals("LPARENT");
    }

    public static boolean IsFuncFParams() {
        return IsFuncFParam();
    }

    public static boolean IsFuncType() {
        return getPreSym().equals("INTTK") || getPreSym().equals("VOIDTK");
    }

    public static boolean IsFuncFParam() {
        return getPreSym().equals("INTTK");
    }

    public static boolean IsConstInitVal() {
        return IsConstExp() || getPreSym().equals("LBRACE");
    }

    public static boolean IsLVal() {
        int k = 1;
        while (getNextSym(k).equals("LBRACK") && !getNextSym(k).equals("EOF")) {
            while (!getNextSym(k).equals("RBRACK")) {
                k++;
            }
            k++;
        }
        return IsIdent() && !getNextSym(k).matches(
                "LPARENT|PLUS|MINU|MULT|DIV|LSS|LEQ|GRE|GEQ|EQL|NEQ|SEMICN");
    }

    public static boolean IsUnaryExp() {
        return IsPrimaryExp() || IsUnaryOp() || IsIdent() && getNextSym(1).equals("LPARENT");
    }

    public static boolean IsIdent() {
        return getPreSym().equals("IDENFR");
    }

    public static boolean IsUnaryOp() {
        return getPreSym().equals("PLUS") || getPreSym().equals("MINU")
                || getPreSym().equals("NOT");
    }

    public static boolean IsPrimaryExp() {
        return getPreSym().equals("LPARENT") || IsIdent() || IsNumber();
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
        return getPreSym().equals("INTTK") && getNextSym(1).equals("IDENFR")
                && !getNextSym(2).equals("LPARENT");
    }

    public static boolean IsVarDef() {
        return getPreSym().equals("IDENFR");
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

    public static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }

    public static String getNextSym(int pos) {
        return AstRecursion.getNextSymToken(pos).getReservedWord();
    }
}
