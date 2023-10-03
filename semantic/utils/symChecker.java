package semantic.utils;

import generation.ErrorController;
import semantic.SemanticAnalysis;
import semantic.SemanticAnalysisChecker;
import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import syntax.AstNode;

public class symChecker {
    private SemanticAnalysisChecker semanticAnalysisChecker;

    public symChecker(SymbolTable symbolTable) {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker(symbolTable);
    }

    public void check(AstNode rootAst) {
        switch (rootAst.getGrammarType()) {
            case "<CompUnit>":
                checkCompUnitChecker(rootAst);
                break;
            case "<ConstDef>":
                checkConstDefChecker(rootAst);
                break;
            case "<VarDef>":
                checkVarDefChecker(rootAst);
                break;
            case "<Block>":
                checkBlockChecker(rootAst);
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
            case "<LVal>":
                checkLValChecker(rootAst);
                break;
            //FuncDef.java
            case "<FuncDef>":
                checkFuncDefChecker(rootAst);
                break;
            case "<FuncFParam>":
                checkFuncFParamChecker(rootAst);
                break;
            //MainFuncDef.java
            case "<MainFuncDef>":
                checkMainFuncDefChecker(rootAst);
                break;
            //Lexer_part
            case "ASSIGN":
                checkASSIGNChecker(rootAst);
                break;
            case "GETINTTK":
                checkGETINTTKChecker(rootAst);
                break;
            default:
                SemanticAnalysis.preTraverse(rootAst);
                break;
        }
    }

    private void checkCompUnitChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(true);
        SymbolTable.createStackSymbolTable();
        SemanticAnalysis.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
    }

    //Definer.java

    private void checkConstDefChecker(AstNode rootAst) {
        Symbol symbol = semanticAnalysisChecker.createConstDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.SymbolError(symbol);
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkVarDefChecker(AstNode rootAst) {
        Symbol symbol = semanticAnalysisChecker.createVarDefChecker(rootAst);
        /*暂时不知道对应错误码，之后修改*/
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.SymbolError(symbol);
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkBlockChecker(AstNode rootAst) {
        SymbolTable.createStackSymbolTable();
        SemanticAnalysis.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
    }

    private void checkForStmtChecker(AstNode sonAst) {
        SymbolTable.enterLoop();
        AstNode rootAst = sonAst.getParent();
        SemanticAnalysis.preTraverse(sonAst);
        SymbolTable.leaveLoop();
    }

    private void checkBreakStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        if (SymbolTable.getLoopLevel() <= 0) {
            /*错误处理*/
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkContinueStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        if (SymbolTable.getLoopLevel() <= 0) {
            /*错误处理*/
        }
        SemanticAnalysis.preTraverse(sonAst);
    }


    /*TODO: 这里可能有bug*/
    private void checkReturnStmtChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        if (rootAst.getChildList().size() >= 2 &&
                rootAst.getChildList().get(1).getGrammarType().equals("<Exp>")) {
            /*错误处理*/
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkLValChecker(AstNode rootAst) {
        if (SymbolTable.getSymByName(rootAst.getChildList().get(0)
                .getSymToken().getWord()) == null) {
            /*错误处理*/
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    //FuncDef.java
    private void checkFuncDefChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(false);
        Symbol symbol = semanticAnalysisChecker.createFuncDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.SymbolError(symbol);
        }
        SymbolTable.setCurrentFunc((FuncSymbol) symbol);
        SymbolTable.createStackSymbolTable();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<Block>")) {
                symDefiner.setParamInfo(astNode, (FuncSymbol) symbol);
            }
            SemanticAnalysis.preTraverse(astNode);
        }
        SymbolTable.destroyStackSymbolTable();
        AstNode block = rootAst.getChildList().get(rootAst.getChildList().size() - 1);
        int senNum = block.getChildList().size();
        AstNode lastSentence = block.getChildList().get(senNum - 2);
        if (!(lastSentence.getGrammarType().equals("RETURNTK")) && symbol.getSymbolType() != Symbol.SymType.VOID) {
            ErrorController.SymbolError(symbol);
        }
    }

    private void checkFuncFParamChecker(AstNode rootAst) {
        Symbol symbol = semanticAnalysisChecker.createFuncFParamChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.SymbolError(symbol);
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    //MainFuncDef.java
    /*TODO: 这个还没写完*/
    private void checkMainFuncDefChecker(AstNode rootAst) {
        SymbolTable.setGlobalArea(false);
        Symbol symbol = semanticAnalysisChecker.createMainFuncDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.SymbolError(symbol);
        }
        SymbolTable.setCurrentFunc((FuncSymbol) symbol);
        SymbolTable.createStackSymbolTable();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<Block>")) {
                symDefiner.setParamInfo(astNode, (FuncSymbol) symbol);
            }
            SemanticAnalysis.preTraverse(rootAst);
        }
        SymbolTable.destroyStackSymbolTable();
        AstNode block = rootAst.getChildList().get(rootAst.getChildList().size() - 1);
        int senNum = block.getChildList().size();
        AstNode lastSentence = block.getChildList().get(senNum - 2);
        if (!(lastSentence.getGrammarType().equals("RETURNTK")) && symbol.getSymbolType() != Symbol.SymType.VOID) {
            ErrorController.SymbolError(symbol);
        }
    }

    //Lexer_part

    private void checkASSIGNChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<LValExp>")) {
                String name = astNode.getChildList().get(0).getSymToken().getWord();
                Symbol symbol = SymbolTable.getSymByName(name);
                if (symbol instanceof ConstSymbol) {
                    ErrorController.SymbolError(symbol);
                }
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkGETINTTKChecker(AstNode sonAst) {
        AstNode rootAst = sonAst.getParent();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<LValExp>")) {
                String name = astNode.getChildList().get(0).getSymToken().getWord();
                Symbol symbol = SymbolTable.getSymByName(name);
                if (symbol instanceof ConstSymbol) {
                    ErrorController.SymbolError(symbol);
                }
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }
}
