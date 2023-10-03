package generation.llvm;

import syntax.AstNode;

public class LLvmGenIR {
    private AstNode rootAst;

    public LLvmGenIR(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void genIrAnalysis(AstNode rootAst) {
        switch (rootAst.getGrammarType()) {
            //Decl.java
            case "<Decl>":
                genIrDeclChecker(rootAst);
                break;
            //Definer.java
            case "<ConstDecl>":
                genIrConstDeclChecker(rootAst);
                break;
            case "<ConstDef>":
                genIrConstDefChecker(rootAst);
                break;
            case "<ConstInitVal>":
                genIrConstInitValChecker(rootAst);
                break;
            case "<VarDecl>":
                genIrVarDeclChecker(rootAst);
                break;
            case "<VarDef>":
                genIrVarDefChecker(rootAst);
                break;
            case "<InitVal>":
                genIrInitValChecker(rootAst);
                break;
            case "<Block>":
                genIrBlockChecker(rootAst);
                break;
            case "<BlockItem>":
                genIrBlockItemChecker(rootAst);
                break;
            case "<Stmt>":
                genIrStmtChecker(rootAst);
                break;
            case "IFTK":
                genIrIfStmtChecker(rootAst);
                break;
            case "FORTK":
                genIrForStmtChecker(rootAst);
                break;
            case "BREAKTK":
                genIrBreakStmtChecker(rootAst);
                break;
            case "CONTINUETK":
                genIrContinueStmtChecker(rootAst);
                break;
            case "RETURNTK":
                genIrReturnStmtChecker(rootAst);
                break;
            case "PRINTFTK":
                genIrPrintStmtChecker(rootAst);
                break;
            case "<ForStmt>":
                genIrForStmtValChecker(rootAst);
                break;
            case "<Exp>":
                genIrExpChecker(rootAst);
                break;
            case "<Cond>":
                genIrCondChecker(rootAst);
                break;
            case "<LVal>":
                genIrLValChecker(rootAst);
                break;
            case "<PrimaryExp>":
                genIrPrimaryExpChecker(rootAst);
                break;
            case "<Number>":
                genIrNumberCallChecker(rootAst);
                break;
            case "<UnaryExp>":
                genIrUnaryExpChecker(rootAst);
                break;
            case "<UnaryOp>":
                genIrUnaryOpChecker(rootAst);
                break;
            case "<FuncRParams>":
                genIrFuncRParamsChecker(rootAst);
                break;
            case "<MulExp>":
                genIrMulExpChecker(rootAst);
                break;
            case "<AddExp>":
                genIrAddExpChecker(rootAst);
                break;
            case "<RelExp>":
                genIrRelExpChecker(rootAst);
                break;
            case "<EqExp>":
                genIrEqExpChecker(rootAst);
                break;
            case "<LAndExp>":
                genIrLAndExpChecker(rootAst);
                break;
            case "<LOrExp>":
                genIrLOrExpChecker(rootAst);
                break;
            case "<ConstExp>":
                genIrConstExpChecker(rootAst);
                break;
            case "<BType>":
                genIrBTypeChecker(rootAst);
                break;
            case "IDENFR":
                genIrIdentChecker(rootAst);
                break;
            //FuncDef.java
            case "<FuncDef>":
                genIrFuncDefChecker(rootAst);
                break;
            case "<FuncType>":
                genIrFuncTypeChecker(rootAst);
                break;
            case "<FuncFParams>":
                genIrFuncFParamsChecker(rootAst);
                break;
            case "<FuncFParam>":
                genIrFuncFParamChecker(rootAst);
                break;
            //MainFuncDef.java
            case "<MainFuncDef>":
                genIrMainFuncDefChecker(rootAst);
                break;
            //Lexer_part
            case "INTTK":
                genIrINTTKChecker(rootAst);
                break;
            case "VOIDTK":
                genIrVOIDTKChecker(rootAst);
                break;
            case "MAINTK":
                genIrMAINTKChecker(rootAst);
                break;
            case "LPARENT":
                genIrLPARENTChecker(rootAst);
                break;
            case "RPARENT":
                genIrRPARENTChecker(rootAst);
                break;
            case "LBRACE":
                genIrLBRACEChecker(rootAst);
                break;
            case "RBRACE":
                genIrRBRACEChecker(rootAst);
                break;
            case "LBRACK":
                genIrLBRACKChecker(rootAst);
                break;
            case "RBRACK":
                genIrRBRACKChecker(rootAst);
                break;
            case "SEMICN":
                genIrSEMICNChecker(rootAst);
                break;
            case "COMMA":
                genIrCOMMAChecker(rootAst);
                break;
            case "ASSIGN":
                genIrASSIGNChecker(rootAst);
                break;
            case "PLUS":
                genIrPLUSChecker(rootAst);
                break;
            case "MINU":
                genIrMINUChecker(rootAst);
                break;
            case "INTCON":
                genIrINTCONChecker(rootAst);
                break;
            case "NOT":
                genIrNOTChecker(rootAst);
                break;
            case "DIV":
                genIrDIVChecker(rootAst);
                break;
            case "MULT":
                genIrMULTChecker(rootAst);
                break;
            case "MOD":
                genIrMODChecker(rootAst);
                break;
            case "AND":
                genIrANDChecker(rootAst);
                break;
            case "OR":
                genIrORChecker(rootAst);
                break;
            case "NEQ":
                genIrNEQChecker(rootAst);
                break;
            case "EQL":
                genIrEQLChecker(rootAst);
                break;
            case "LSS":
                genIrLSSChecker(rootAst);
                break;
            case "LEQ":
                genIrLEQChecker(rootAst);
                break;
            case "GRE":
                genIrGREChecker(rootAst);
                break;
            case "GEQ":
                genIrGEQChecker(rootAst);
                break;
            case "STRCON":
                genIrSTRCONChecker(rootAst);
                break;
            case "CONSTTK":
                genIrCONSTTKChecker(rootAst);
                break;
            case "ELSETK":
                genIrELSETKChecker(rootAst);
                break;
            case "GETINTTK":
                genIrGETINTTKChecker(rootAst);
                break;
            default:
                break;
        }
    }

    //Decl.java
    private void genIrDeclChecker(AstNode rootAst) {
    }

    //Definer.java
    private void genIrConstDeclChecker(AstNode rootAst) {
    }

    private void genIrConstDefChecker(AstNode rootAst) {
    }

    private void genIrConstInitValChecker(AstNode rootAst) {
    }

    private void genIrVarDeclChecker(AstNode rootAst) {
    }

    private void genIrVarDefChecker(AstNode rootAst) {
    }

    private void genIrInitValChecker(AstNode rootAst) {
    }

    private void genIrBlockChecker(AstNode rootAst) {
    }

    private void genIrBlockItemChecker(AstNode rootAst) {
    }

    private void genIrStmtChecker(AstNode rootAst) {
    }

    private void genIrIfStmtChecker(AstNode rootAst) {
    }

    private void genIrForStmtChecker(AstNode rootAst) {
    }

    private void genIrBreakStmtChecker(AstNode rootAst) {
    }

    private void genIrContinueStmtChecker(AstNode rootAst) {
    }

    private void genIrReturnStmtChecker(AstNode rootAst) {
    }

    private void genIrPrintStmtChecker(AstNode rootAst) {
    }

    private void genIrForStmtValChecker(AstNode rootAst) {
    }

    private void genIrExpChecker(AstNode rootAst) {
    }

    private void genIrCondChecker(AstNode rootAst) {
    }

    private void genIrLValChecker(AstNode rootAst) {
    }

    private void genIrPrimaryExpChecker(AstNode rootAst) {
    }

    private void genIrNumberCallChecker(AstNode rootAst) {
    }

    private void genIrUnaryExpChecker(AstNode rootAst) {
    }

    private void genIrUnaryOpChecker(AstNode rootAst) {
    }

    private void genIrFuncRParamsChecker(AstNode rootAst) {
    }

    private void genIrMulExpChecker(AstNode rootAst) {
    }

    private void genIrAddExpChecker(AstNode rootAst) {
    }

    private void genIrRelExpChecker(AstNode rootAst) {
    }

    private void genIrEqExpChecker(AstNode rootAst) {
    }

    private void genIrLAndExpChecker(AstNode rootAst) {
    }

    private void genIrLOrExpChecker(AstNode rootAst) {
    }

    private void genIrConstExpChecker(AstNode rootAst) {
    }

    private void genIrBTypeChecker(AstNode rootAst) {
    }

    private void genIrIdentChecker(AstNode rootAst) {
    }

    //FuncDef.java
    private void genIrFuncDefChecker(AstNode rootAst) {
    }

    private void genIrFuncTypeChecker(AstNode rootAst) {
    }

    private void genIrFuncFParamsChecker(AstNode rootAst) {
    }

    private void genIrFuncFParamChecker(AstNode rootAst) {
    }

    //MainFuncDef.java
    private void genIrMainFuncDefChecker(AstNode rootAst) {
    }

    //Lexer_part
    private void genIrINTTKChecker(AstNode rootAst) {
    }

    private void genIrVOIDTKChecker(AstNode rootAst) {
    }

    private void genIrMAINTKChecker(AstNode rootAst) {
    }

    private void genIrLPARENTChecker(AstNode rootAst) {
    }

    private void genIrRPARENTChecker(AstNode rootAst) {
    }

    private void genIrLBRACEChecker(AstNode rootAst) {
    }

    private void genIrRBRACEChecker(AstNode rootAst) {
    }

    private void genIrLBRACKChecker(AstNode rootAst) {
    }

    private void genIrRBRACKChecker(AstNode rootAst) {
    }

    private void genIrSEMICNChecker(AstNode rootAst) {
    }

    private void genIrCOMMAChecker(AstNode rootAst) {
    }

    private void genIrASSIGNChecker(AstNode rootAst) {
    }

    private void genIrPLUSChecker(AstNode rootAst) {
    }

    private void genIrMINUChecker(AstNode rootAst) {
    }

    private void genIrINTCONChecker(AstNode rootAst) {
    }

    private void genIrNOTChecker(AstNode rootAst) {
    }

    private void genIrDIVChecker(AstNode rootAst) {
    }

    private void genIrMULTChecker(AstNode rootAst) {
    }

    private void genIrMODChecker(AstNode rootAst) {
    }

    private void genIrANDChecker(AstNode rootAst) {
    }

    private void genIrORChecker(AstNode rootAst) {
    }

    private void genIrNEQChecker(AstNode rootAst) {
    }

    private void genIrEQLChecker(AstNode rootAst) {
    }

    private void genIrLSSChecker(AstNode rootAst) {
    }

    private void genIrLEQChecker(AstNode rootAst) {
    }

    private void genIrGREChecker(AstNode rootAst) {
    }

    private void genIrGEQChecker(AstNode rootAst) {
    }

    private void genIrSTRCONChecker(AstNode rootAst) {
    }

    private void genIrCONSTTKChecker(AstNode rootAst) {
    }

    private void genIrELSETKChecker(AstNode rootAst) {
    }

    private void genIrGETINTTKChecker(AstNode rootAst) {
    }
}
