package syntax.semantic;

import syntax.AstNode;

public class SemanticAnalysisChecker {
    private AstNode rootAst;

    public SemanticAnalysisChecker(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void checkAnalysis(AstNode rootAst) {
        switch (rootAst.getGrammarType()) {
            //Decl.java
            case "<Decl>":
                checkDeclChecker(rootAst);
                break;
            //Definer.java
            case "<ConstDecl>":
                checkConstDeclChecker(rootAst);
                break;
            case "<ConstDef>":
                checkConstDefChecker(rootAst);
                break;
            case "<ConstInitVal>":
                checkConstInitValChecker(rootAst);
                break;
            case "<VarDecl>":
                checkVarDeclChecker(rootAst);
                break;
            case "<VarDef>":
                checkVarDefChecker(rootAst);
                break;
            case "<InitVal>":
                checkInitValChecker(rootAst);
                break;
            case "<Block>":
                checkBlockChecker(rootAst);
                break;
            case "<BlockItem>":
                checkBlockItemChecker(rootAst);
                break;
            case "<Stmt>":
                checkStmtChecker(rootAst);
                break;
            case "IFTK":
                checkIfStmtChecker(rootAst);
                break;
            case "FORTK":
                checkForStmtChecker(rootAst);
                break;
            case "BREAKTK":
                checkBreakStmtChecker(rootAst);
                break;
            case "CONTINUETK":
                checkContinueStmtChecker(rootAst);
                break;
            case "RETURNTK":
                checkReturnStmtChecker(rootAst);
                break;
            case "PRINTFTK":
                checkPrintStmtChecker(rootAst);
                break;
            case "<ForStmt>":
                checkForStmtValChecker(rootAst);
                break;
            case "<Exp>":
                checkExpChecker(rootAst);
                break;
            case "<Cond>":
                checkCondChecker(rootAst);
                break;
            case "<LVal>":
                checkLValChecker(rootAst);
                break;
            case "<PrimaryExp>":
                checkPrimaryExpChecker(rootAst);
                break;
            case "<Number>":
                checkNumberCallChecker(rootAst);
                break;
            case "<UnaryExp>":
                checkUnaryExpChecker(rootAst);
                break;
            case "<UnaryOp>":
                checkUnaryOpChecker(rootAst);
                break;
            case "<FuncRParams>":
                checkFuncRParamsChecker(rootAst);
                break;
            case "<MulExp>":
                checkMulExpChecker(rootAst);
                break;
            case "<AddExp>":
                checkAddExpChecker(rootAst);
                break;
            case "<RelExp>":
                checkRelExpChecker(rootAst);
                break;
            case "<EqExp>":
                checkEqExpChecker(rootAst);
                break;
            case "<LAndExp>":
                checkLAndExpChecker(rootAst);
                break;
            case "<LOrExp>":
                checkLOrExpChecker(rootAst);
                break;
            case "<ConstExp>":
                checkConstExpChecker(rootAst);
                break;
            case "<BType>":
                checkBTypeChecker(rootAst);
                break;
            case "IDENFR":
                checkIdentChecker(rootAst);
                break;
            //FuncDef.java
            case "<FuncDef>":
                checkFuncDefChecker(rootAst);
                break;
            case "<FuncType>":
                checkFuncTypeChecker(rootAst);
                break;
            case "<FuncFParams>":
                checkFuncFParamsChecker(rootAst);
                break;
            case "<FuncFParam>":
                checkFuncFParamChecker(rootAst);
                break;
            //MainFuncDef.java
            case "<MainFuncDef>":
                checkMainFuncDefChecker(rootAst);
                break;
            //Lexer_part
            case "INTTK":
                checkINTTKChecker(rootAst);
                break;
            case "VOIDTK":
                checkVOIDTKChecker(rootAst);
                break;
            case "MAINTK":
                checkMAINTKChecker(rootAst);
                break;
            case "LPARENT":
                checkLPARENTChecker(rootAst);
                break;
            case "RPARENT":
                checkRPARENTChecker(rootAst);
                break;
            case "LBRACE":
                checkLBRACEChecker(rootAst);
                break;
            case "RBRACE":
                checkRBRACEChecker(rootAst);
                break;
            case "LBRACK":
                checkLBRACKChecker(rootAst);
                break;
            case "RBRACK":
                checkRBRACKChecker(rootAst);
                break;
            case "SEMICN":
                checkSEMICNChecker(rootAst);
                break;
            case "COMMA":
                checkCOMMAChecker(rootAst);
                break;
            case "ASSIGN":
                checkASSIGNChecker(rootAst);
                break;
            case "PLUS":
                checkPLUSChecker(rootAst);
                break;
            case "MINU":
                checkMINUChecker(rootAst);
                break;
            case "INTCON":
                checkINTCONChecker(rootAst);
                break;
            case "NOT":
                checkNOTChecker(rootAst);
                break;
            case "DIV":
                checkDIVChecker(rootAst);
                break;
            case "MULT":
                checkMULTChecker(rootAst);
                break;
            case "MOD":
                checkMODChecker(rootAst);
                break;
            case "AND":
                checkANDChecker(rootAst);
                break;
            case "OR":
                checkORChecker(rootAst);
                break;
            case "NEQ":
                checkNEQChecker(rootAst);
                break;
            case "EQL":
                checkEQLChecker(rootAst);
                break;
            case "LSS":
                checkLSSChecker(rootAst);
                break;
            case "LEQ":
                checkLEQChecker(rootAst);
                break;
            case "GRE":
                checkGREChecker(rootAst);
                break;
            case "GEQ":
                checkGEQChecker(rootAst);
                break;
            case "STRCON":
                checkSTRCONChecker(rootAst);
                break;
            case "CONSTTK":
                checkCONSTTKChecker(rootAst);
                break;
            case "ELSETK":
                checkELSETKChecker(rootAst);
                break;
            case "GETINTTK":
                checkGETINTTKChecker(rootAst);
                break;
            default:
                break;
        }
    }


    //Decl.java
    private void checkDeclChecker(AstNode rootAst) {
    }

    //Definer.java
    private void checkConstDeclChecker(AstNode rootAst) {
    }

    private void checkConstDefChecker(AstNode rootAst) {
    }

    private void checkConstInitValChecker(AstNode rootAst) {
    }

    private void checkVarDeclChecker(AstNode rootAst) {
    }

    private void checkVarDefChecker(AstNode rootAst) {
    }

    private void checkInitValChecker(AstNode rootAst) {
    }

    private void checkBlockChecker(AstNode rootAst) {
    }

    private void checkBlockItemChecker(AstNode rootAst) {
    }

    private void checkStmtChecker(AstNode rootAst) {
    }

    private void checkIfStmtChecker(AstNode rootAst) {
    }

    private void checkForStmtChecker(AstNode rootAst) {
    }

    private void checkBreakStmtChecker(AstNode rootAst) {
    }

    private void checkContinueStmtChecker(AstNode rootAst) {
    }

    private void checkReturnStmtChecker(AstNode rootAst) {
    }

    private void checkPrintStmtChecker(AstNode rootAst) {
    }

    private void checkForStmtValChecker(AstNode rootAst) {
    }

    private void checkExpChecker(AstNode rootAst) {
    }

    private void checkCondChecker(AstNode rootAst) {
    }

    private void checkLValChecker(AstNode rootAst) {
    }

    private void checkPrimaryExpChecker(AstNode rootAst) {
    }

    private void checkNumberCallChecker(AstNode rootAst) {
    }

    private void checkUnaryExpChecker(AstNode rootAst) {
    }

    private void checkUnaryOpChecker(AstNode rootAst) {
    }

    private void checkFuncRParamsChecker(AstNode rootAst) {
    }

    private void checkMulExpChecker(AstNode rootAst) {
    }

    private void checkAddExpChecker(AstNode rootAst) {
    }

    private void checkRelExpChecker(AstNode rootAst) {
    }

    private void checkEqExpChecker(AstNode rootAst) {
    }

    private void checkLAndExpChecker(AstNode rootAst) {
    }

    private void checkLOrExpChecker(AstNode rootAst) {
    }

    private void checkConstExpChecker(AstNode rootAst) {
    }

    private void checkBTypeChecker(AstNode rootAst) {
    }

    private void checkIdentChecker(AstNode rootAst) {
    }

    //FuncDef.java
    private void checkFuncDefChecker(AstNode rootAst) {
    }

    private void checkFuncTypeChecker(AstNode rootAst) {
    }

    private void checkFuncFParamsChecker(AstNode rootAst) {
    }

    private void checkFuncFParamChecker(AstNode rootAst) {
    }

    //MainFuncDef.java
    private void checkMainFuncDefChecker(AstNode rootAst) {
    }

    //Lexer_part
    private void checkINTTKChecker(AstNode rootAst) {
    }

    private void checkVOIDTKChecker(AstNode rootAst) {
    }

    private void checkMAINTKChecker(AstNode rootAst) {
    }

    private void checkLPARENTChecker(AstNode rootAst) {
    }

    private void checkRPARENTChecker(AstNode rootAst) {
    }

    private void checkLBRACEChecker(AstNode rootAst) {
    }

    private void checkRBRACEChecker(AstNode rootAst) {
    }

    private void checkLBRACKChecker(AstNode rootAst) {
    }

    private void checkRBRACKChecker(AstNode rootAst) {
    }

    private void checkSEMICNChecker(AstNode rootAst) {
    }

    private void checkCOMMAChecker(AstNode rootAst) {
    }

    private void checkASSIGNChecker(AstNode rootAst) {
    }

    private void checkPLUSChecker(AstNode rootAst) {
    }

    private void checkMINUChecker(AstNode rootAst) {
    }

    private void checkINTCONChecker(AstNode rootAst) {
    }

    private void checkNOTChecker(AstNode rootAst) {
    }

    private void checkDIVChecker(AstNode rootAst) {
    }

    private void checkMULTChecker(AstNode rootAst) {
    }

    private void checkMODChecker(AstNode rootAst) {
    }

    private void checkANDChecker(AstNode rootAst) {
    }

    private void checkORChecker(AstNode rootAst) {
    }

    private void checkNEQChecker(AstNode rootAst) {
    }

    private void checkEQLChecker(AstNode rootAst) {
    }

    private void checkLSSChecker(AstNode rootAst) {
    }

    private void checkLEQChecker(AstNode rootAst) {
    }

    private void checkGREChecker(AstNode rootAst) {
    }

    private void checkGEQChecker(AstNode rootAst) {
    }

    private void checkSTRCONChecker(AstNode rootAst) {
    }

    private void checkCONSTTKChecker(AstNode rootAst) {
    }

    private void checkELSETKChecker(AstNode rootAst) {
    }

    private void checkGETINTTKChecker(AstNode rootAst) {
    }
}
