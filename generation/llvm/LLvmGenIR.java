package generation.llvm;

import generation.GenerationMain;
import generation.value.Value;
import syntax.AstNode;

public class LLvmGenIR {

    public LLvmGenIR() {
    }

    public Value genIrAnalysis(AstNode rootAst) {
        return switch (rootAst.getGrammarType()) {
            //Decl.java
            case "<Decl>" -> genIrDeclChecker(rootAst);
            //Definer.java
            case "<ConstDecl>" -> genIrConstDeclChecker(rootAst);
            case "<ConstDef>" -> genIrConstDefChecker(rootAst);
            case "<ConstInitVal>" -> genIrConstInitValChecker(rootAst);
            case "<VarDecl>" -> genIrVarDeclChecker(rootAst);
            case "<VarDef>" -> genIrVarDefChecker(rootAst);
            case "<InitVal>" -> genIrInitValChecker(rootAst);
            case "<Block>" -> genIrBlockChecker(rootAst);
            case "<BlockItem>" -> genIrBlockItemChecker(rootAst);
            case "<Stmt>" -> genIrStmtChecker(rootAst);
            case "IFTK" -> genIrIfStmtChecker(rootAst);
            case "FORTK" -> genIrForStmtChecker(rootAst);
            case "BREAKTK" -> genIrBreakStmtChecker(rootAst);
            case "CONTINUETK" -> genIrContinueStmtChecker(rootAst);
            case "RETURNTK" -> genIrReturnStmtChecker(rootAst);
            case "PRINTFTK" -> genIrPrintStmtChecker(rootAst);
            case "<ForStmt>" -> genIrForStmtValChecker(rootAst);
            case "<Exp>" -> genIrExpChecker(rootAst);
            case "<Cond>" -> genIrCondChecker(rootAst);
            case "<LVal>" -> genIrLValChecker(rootAst);
            case "<PrimaryExp>" -> genIrPrimaryExpChecker(rootAst);
            case "<Number>" -> genIrNumberCallChecker(rootAst);
            case "<UnaryExp>" -> genIrUnaryExpChecker(rootAst);
            case "<UnaryOp>" -> genIrUnaryOpChecker(rootAst);
            case "<FuncRParams>" -> genIrFuncRParamsChecker(rootAst);
            case "<MulExp>" -> genIrMulExpChecker(rootAst);
            case "<AddExp>" -> genIrAddExpChecker(rootAst);
            case "<RelExp>" -> genIrRelExpChecker(rootAst);
            case "<EqExp>" -> genIrEqExpChecker(rootAst);
            case "<LAndExp>" -> genIrLAndExpChecker(rootAst);
            case "<LOrExp>" -> genIrLOrExpChecker(rootAst);
            case "<ConstExp>" -> genIrConstExpChecker(rootAst);
            case "<BType>" -> genIrBTypeChecker(rootAst);
            case "IDENFR" -> genIrIdentChecker(rootAst);
            //FuncDef.java
            case "<FuncDef>" -> genIrFuncDefChecker(rootAst);
            case "<FuncType>" -> genIrFuncTypeChecker(rootAst);
            case "<FuncFParams>" -> genIrFuncFParamsChecker(rootAst);
            case "<FuncFParam>" -> genIrFuncFParamChecker(rootAst);
            //MainFuncDef.java
            case "<MainFuncDef>" -> genIrMainFuncDefChecker(rootAst);
            //Lexer_part
            case "INTTK" -> genIrINTTKChecker(rootAst);
            case "VOIDTK" -> genIrVOIDTKChecker(rootAst);
            case "MAINTK" -> genIrMAINTKChecker(rootAst);
            case "LPARENT" -> genIrLPARENTChecker(rootAst);
            case "RPARENT" -> genIrRPARENTChecker(rootAst);
            case "LBRACE" -> genIrLBRACEChecker(rootAst);
            case "RBRACE" -> genIrRBRACEChecker(rootAst);
            case "LBRACK" -> genIrLBRACKChecker(rootAst);
            case "RBRACK" -> genIrRBRACKChecker(rootAst);
            case "SEMICN" -> genIrSEMICNChecker(rootAst);
            case "COMMA" -> genIrCOMMAChecker(rootAst);
            case "ASSIGN" -> genIrASSIGNChecker(rootAst);
            case "PLUS" -> genIrPLUSChecker(rootAst);
            case "MINU" -> genIrMINUChecker(rootAst);
            case "INTCON" -> genIrINTCONChecker(rootAst);
            case "NOT" -> genIrNOTChecker(rootAst);
            case "DIV" -> genIrDIVChecker(rootAst);
            case "MULT" -> genIrMULTChecker(rootAst);
            case "MOD" -> genIrMODChecker(rootAst);
            case "AND" -> genIrANDChecker(rootAst);
            case "OR" -> genIrORChecker(rootAst);
            case "NEQ" -> genIrNEQChecker(rootAst);
            case "EQL" -> genIrEQLChecker(rootAst);
            case "LSS" -> genIrLSSChecker(rootAst);
            case "LEQ" -> genIrLEQChecker(rootAst);
            case "GRE" -> genIrGREChecker(rootAst);
            case "GEQ" -> genIrGEQChecker(rootAst);
            case "STRCON" -> genIrSTRCONChecker(rootAst);
            case "CONSTTK" -> genIrCONSTTKChecker(rootAst);
            case "ELSETK" -> genIrELSETKChecker(rootAst);
            case "GETINTTK" -> genIrGETINTTKChecker(rootAst);
            default -> {
                GenerationMain.preTraverse(rootAst);
                yield null;
            }
        };
    }

    //Decl.java
    private Value genIrDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //Definer.java
    private Value genIrConstDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVarDeclChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVarDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrInitValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBlockChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBlockItemChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrIfStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrForStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBreakStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrContinueStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrReturnStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrPrintStmtChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrForStmtValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrCondChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLValChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrPrimaryExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrNumberCallChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrUnaryExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrUnaryOpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncRParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMulExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrAddExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRelExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrEqExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLAndExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLOrExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrConstExpChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrBTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrIdentChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //FuncDef.java
    private Value genIrFuncDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncTypeChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncFParamsChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrFuncFParamChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //MainFuncDef.java
    private Value genIrMainFuncDefChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    //Lexer_part
    private Value genIrINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrVOIDTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMAINTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRPARENTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRBRACEChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrRBRACKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrSEMICNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrCOMMAChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrASSIGNChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrPLUSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMINUChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrINTCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrNOTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrDIVChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMULTChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrMODChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrANDChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrORChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrNEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrEQLChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLSSChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrLEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGREChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGEQChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrSTRCONChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrCONSTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrELSETKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }

    private Value genIrGETINTTKChecker(AstNode rootAst) {
        GenerationMain.preTraverse(rootAst);
        return null;
    }
}
