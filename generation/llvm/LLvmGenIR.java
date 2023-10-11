package generation.llvm;

import generation.GenerationMain;
import syntax.AstNode;

public class LLvmGenIR {

    public LLvmGenIR() {
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
                GenerationMain.preTraverse(rootAst);
                break;
        }
    }

    //Decl.java
    private void genIrDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    //Definer.java
    private void genIrConstDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrConstDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrConstInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrVarDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrVarDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrBlockChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrBlockItemChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrIfStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrForStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrBreakStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrContinueStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrReturnStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrPrintStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrForStmtValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrCondChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrPrimaryExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrNumberCallChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrUnaryExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrUnaryOpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrFuncRParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrMulExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrAddExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrRelExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrEqExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLAndExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLOrExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrConstExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrBTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrIdentChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    //FuncDef.java
    private void genIrFuncDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrFuncTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrFuncFParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrFuncFParamChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    //MainFuncDef.java
    private void genIrMainFuncDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    //Lexer_part
    private void genIrINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrVOIDTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrMAINTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrRPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrRBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrRBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrSEMICNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrCOMMAChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrASSIGNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrPLUSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrMINUChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrINTCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrNOTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrDIVChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrMULTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrMODChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrANDChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrORChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrNEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrEQLChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLSSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrLEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrGREChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrGEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrSTRCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrCONSTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrELSETKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }

    private void genIrGETINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
    }
}
