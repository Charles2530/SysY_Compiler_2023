package semantic.utils;

import generation.utils.ErrorController;
import generation.utils.ErrorToken;
import generation.utils.OutputController;
import semantic.SemanticAnalysis;
import semantic.SemanticAnalysisChecker;
import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import semantic.symbolTable.symbol.VarSymbol;
import syntax.AstNode;

import java.io.IOException;
import java.util.ArrayList;

public class symChecker {
    private SemanticAnalysisChecker semanticAnalysisChecker;

    public symChecker() {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker();
    }

    public void check(AstNode rootAst) throws IOException {
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
            case "<FuncDef>":
                checkFuncDefChecker(rootAst);
                break;
            case "<FuncFParam>":
                checkFuncFParamChecker(rootAst);
                break;
            case "<MainFuncDef>":
                checkMainFuncDefChecker(rootAst);
                break;
            case "ASSIGN":
                checkASSIGNChecker(rootAst);
                break;
            case "GETINTTK":
                checkGETINTTKChecker(rootAst);
                break;
            case "<UnaryExp>":
                checkUnaryExpChecker(rootAst);
                break;
            case "PRINTFTK":
                checkPRINTFTKChecker(rootAst);
                break;
            default:
                SemanticAnalysis.preTraverse(rootAst);
                break;
        }
    }

    private void checkCompUnitChecker(AstNode rootAst) throws IOException {
        SymbolTable.setGlobalArea(true);
        SymbolTable.createStackSymbolTable();
        SemanticAnalysis.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
    }

    //Definer.java

    private void checkConstDefChecker(AstNode rootAst) throws IOException {
        Symbol symbol = semanticAnalysisChecker.createConstDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getStartLine()));
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkVarDefChecker(AstNode rootAst) throws IOException {
        Symbol symbol = semanticAnalysisChecker.createVarDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getStartLine()));
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkBlockChecker(AstNode rootAst) throws IOException {
        SymbolTable.createStackSymbolTable();
        SemanticAnalysis.preTraverse(rootAst);
        SymbolTable.destroyStackSymbolTable();
    }

    private void checkForStmtChecker(AstNode sonAst) throws IOException {
        SymbolTable.enterLoop();
        SemanticAnalysis.preTraverse(sonAst);
        SymbolTable.leaveLoop();
    }

    private void checkBreakStmtChecker(AstNode sonAst) throws IOException {
        AstNode rootAst = sonAst.getParent();
        if (SymbolTable.getLoopLevel() <= 0) {
            ErrorController.addError(new ErrorToken("m", rootAst.getSpan().getEndLine()));
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkContinueStmtChecker(AstNode sonAst) throws IOException {
        AstNode rootAst = sonAst.getParent();
        if (SymbolTable.getLoopLevel() <= 0) {
            ErrorController.addError(new ErrorToken("m", rootAst.getSpan().getEndLine()));
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    /*TODO: 这里可能有bug*/
    private void checkReturnStmtChecker(AstNode sonAst) throws IOException {
        AstNode rootAst = sonAst.getParent();
        if (rootAst.getChildList().size() >= 2 &&
                rootAst.getChildList().get(1).getGrammarType().equals("<Exp>")) {
            FuncSymbol func = SymbolTable.getCurrentFunc();
            if (func.getReturnType() == Symbol.SymType.VOID) {
                ErrorController.addError(new ErrorToken("f", rootAst.getSpan().getEndLine()));
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkLValChecker(AstNode rootAst) throws IOException {
        if (SymbolTable.getSymByName(rootAst.getChildList().get(0)
                .getSymToken().getWord()) == null) {
            ErrorController.addError(new ErrorToken("c", rootAst.getSpan().getEndLine()));
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    //FuncDef.java
    private void checkFuncDefChecker(AstNode rootAst) throws IOException {
        SymbolTable.setGlobalArea(false);
        Symbol symbol = semanticAnalysisChecker.createFuncDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getStartLine()));
        }
        SymbolTable.setCurrentFunc((FuncSymbol) symbol);
        SymbolTable.createStackSymbolTable();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<Block>")) {
                symDefiner.setParamInfo(rootAst, (FuncSymbol) symbol);
            }
            SemanticAnalysis.preTraverse(astNode);
        }
        SymbolTable.destroyStackSymbolTable();
        AstNode block = rootAst.getChildList().get(rootAst.getChildList().size() - 1);
        int senNum = block.getChildList().size();
        if (symbol.getSymbolType() != Symbol.SymType.VOID) {
            AstNode lastSentence = block.getChildList().get(senNum - 2)
                    .getChildList().get(0).getChildList().get(0);
            if (!(lastSentence.getGrammarType().equals("RETURNTK"))) {
                ErrorController.addError(new ErrorToken("g", rootAst.getSpan().getEndLine()));
            }
        }
    }

    private void checkFuncFParamChecker(AstNode rootAst) throws IOException {
        Symbol symbol = semanticAnalysisChecker.createFuncFParamChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getEndLine()));
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    //MainFuncDef.java
    /*TODO: 这个还没写完*/
    private void checkMainFuncDefChecker(AstNode rootAst) throws IOException {
        SymbolTable.setGlobalArea(false);
        Symbol symbol = semanticAnalysisChecker.createMainFuncDefChecker(rootAst);
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getStartLine()));
        }
        SymbolTable.setCurrentFunc((FuncSymbol) symbol);
        SymbolTable.createStackSymbolTable();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<Block>")) {
                symDefiner.setParamInfo(rootAst, (FuncSymbol) symbol);
            }
            SemanticAnalysis.preTraverse(astNode);
        }
        SymbolTable.destroyStackSymbolTable();
        AstNode block = rootAst.getChildList().get(rootAst.getChildList().size() - 1);
        int senNum = block.getChildList().size();
        if (symbol.getSymbolType() != Symbol.SymType.VOID) {
            AstNode lastSentence = block.getChildList().get(senNum - 2)
                    .getChildList().get(0).getChildList().get(0);
            if (!(lastSentence.getGrammarType().equals("RETURNTK"))) {
                ErrorController.addError(new ErrorToken("g", rootAst.getSpan().getEndLine()));
            }
        }
    }

    //Lexer_part

    private void checkASSIGNChecker(AstNode sonAst) throws IOException {
        Symbol symbol = null;
        ArrayList<Integer> tempDim = new ArrayList<>();
        AstNode rootAst = sonAst.getParent();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<LVal>")) {
                for (AstNode child : astNode.getChildList()) {
                    if (child.getGrammarType().equals("IDENFR")) {
                        String name = child.getSymToken().getWord();
                        symbol = SymbolTable.getSymByName(name);
                    } else if (child.getGrammarType().equals("<Exp>")) {
                        if (tempDim.size() != ((VarSymbol) symbol).getDim()) {
                            tempDim.add(symCalc.calc(child));
                        }
                    }
                }
                if (symbol instanceof ConstSymbol) {
                    ErrorController.addError(new ErrorToken("h", rootAst.getSpan().getEndLine()));
                }
            } else if (astNode.getGrammarType().equals("<Exp>")) {
                if (OutputController.getIsCalcMode()) {
                    if (symbol instanceof VarSymbol) {
                        int value = symCalc.calc(astNode);
                        if (tempDim.isEmpty()) {
                            ((VarSymbol) symbol).updateValue(value);
                        } else if (tempDim.size() == 1) {
                            ((VarSymbol) symbol).updateValue(value, tempDim.get(0));
                        } else {
                            ((VarSymbol) symbol).updateValue(value, tempDim.get(0), tempDim.get(1));
                        }
                    }
                }
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkGETINTTKChecker(AstNode sonAst) throws IOException {
        AstNode rootAst = sonAst.getParent();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<LVal>")) {
                String name = astNode.getChildList().get(0).getSymToken().getWord();
                Symbol symbol = SymbolTable.getSymByName(name);
                if (symbol instanceof ConstSymbol) {
                    ErrorController.addError(new ErrorToken("h",
                            rootAst.getSpan().getEndLine()));
                }
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkUnaryExpChecker(AstNode rootAst) throws IOException {
        if (rootAst.getChildList().size() >= 3 && rootAst.getChildList().get(1).
                getGrammarType().equals("LPARENT")) {
            FuncSymbol funcSymbol = (FuncSymbol) SymbolTable.getSymByName(
                    rootAst.getChildList().get(0).getSymToken().getWord());
            if (funcSymbol == null) {
                ErrorController.addError(new ErrorToken("c", rootAst.getSpan().getEndLine()));
            } else {
                int paramNum = funcSymbol.getParamNum();
                int argNum = rootAst.getChildList().get(2).getChildList().size();
                if (paramNum != argNum) {
                    ErrorController.addError(new ErrorToken("d", rootAst.getSpan().getStartLine()));
                } else {
                    for (int i = 0; i < paramNum; i++) {
                        Symbol.SymType paramType = funcSymbol.getFParamTypes().get(i);
                        Symbol.SymType argType = symType.getExpType(rootAst.getChildList().
                                get(2).getChildList().get(i));
                        if (paramType != argType) {
                            ErrorController.addError(new ErrorToken("e",
                                    rootAst.getSpan().getStartLine()));
                        }
                    }
                }
            }
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkPRINTFTKChecker(AstNode astNode) throws IOException {
        AstNode rootAst = astNode.getParent();
        int argNum = 0;
        String formatString = rootAst.getChildList().get(2).getSymToken().getWord();
        for (int i = 0; i < formatString.length(); i++) {
            if (formatString.charAt(i) == '%' && i + 1 < formatString.length()
                    && formatString.charAt(i + 1) == 'd') {
                argNum++;
            }
        }
        int inputNum = 0;
        for (AstNode child : rootAst.getChildList()) {
            if (child.getGrammarType().equals("<Exp>")) {
                inputNum++;
            }
        }
        if (argNum != inputNum) {
            ErrorController.addError(new ErrorToken("l", rootAst.getSpan().getEndLine()));
        }
        SemanticAnalysis.preTraverse(astNode);
    }
}
