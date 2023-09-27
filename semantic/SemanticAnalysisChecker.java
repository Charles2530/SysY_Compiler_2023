package semantic;

import syntax.AstNode;

public class SemanticAnalysisChecker {
    private AstNode rootAst;

    public SemanticAnalysisChecker(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void checkAnalysis(AstNode rootAst) {
        switch (rootAst.getGrammarType()) {
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
            case "IfStmt":
                checkIfStmtChecker(rootAst);
                break;
            case "FORTK":
                checkForStmtChecker(rootAst);
                break;
            case "<BreakStmt>":
                checkBreakStmtChecker(rootAst);
                break;
            case "<ContinueStmt>":
                checkContinueStmtChecker(rootAst);
                break;
            case "<ReturnStmt>":
                checkReturnStmtChecker(rootAst);
                break;
            case "<PrintStmt>":
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
            case "UnaryExp":
                checkUnaryExpChecker(rootAst);
                break;
            case "<UnaryOp>":
                checkUnaryOpChecker(rootAst);
                break;
            case "<FuncRParams>":
                checkFuncRParamsChecker(rootAst);
                break;
            case "MulExp":
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
            case "IDENFR":
                checkIdentChecker(rootAst);
                break;
            default:
                break;
        }
    }

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

    private void checkIdentChecker(AstNode rootAst) {
    }
}
