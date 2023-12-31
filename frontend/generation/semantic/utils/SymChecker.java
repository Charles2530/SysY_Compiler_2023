package frontend.generation.semantic.utils;

import iostream.structure.ErrorController;
import iostream.structure.ErrorToken;
import iostream.structure.OutputController;
import frontend.generation.semantic.SemanticAnalysis;
import frontend.generation.semantic.SemanticAnalysisChecker;
import frontend.generation.semantic.symtable.Symbol;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.varsymbol.ConstSymbol;
import frontend.generation.semantic.symtable.symbol.FuncSymbol;
import frontend.generation.semantic.symtable.symbol.varsymbol.IntSymbol;
import frontend.generation.syntax.AstNode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * SymChecker 是语义分析用于检查符号表表项是否存在语义错误或语法错误的检查器
 */
public class SymChecker {
    /**
     * semanticAnalysisChecker 是语义分析用于创建符号表表项的检查器,
     * 这里将其引入方便在每次创建表项后进行检查
     */
    private final SemanticAnalysisChecker semanticAnalysisChecker;

    public SymChecker() {
        this.semanticAnalysisChecker = new SemanticAnalysisChecker();
    }

    /**
     * check 是错误处理检查的核心主函数，会在检查到语法或语义错误后将其
     * 填入ErrorController中，下方定义了其对应的各类子函数
     */
    public void check(AstNode rootAst) throws IOException {
        switch (rootAst.getGrammarType()) {
            case "<CompUnit>":
                checkCompUnitChecker(rootAst);
                break;
            case "<ConstDef>":
                // Error b
                checkConstDefChecker(rootAst);
                break;
            case "<VarDef>":
                // Error b
                checkVarDefChecker(rootAst);
                break;
            case "<Block>":
                checkBlockChecker(rootAst);
                break;
            case "FORTK":
                checkForStmtChecker(rootAst);
                break;
            case "BREAKTK":
                // Error m
                checkBreakStmtChecker(rootAst);
                break;
            case "CONTINUETK":
                // Error m
                checkContinueStmtChecker(rootAst);
                break;
            case "RETURNTK":
                // Error f
                checkReturnStmtChecker(rootAst);
                break;
            case "<LVal>":
                // Error c
                checkLValChecker(rootAst);
                break;
            case "<FuncDef>":
                // Error b,g
                checkFuncDefChecker(rootAst);
                break;
            case "<FuncFParam>":
                // Error b
                checkFuncFParamChecker(rootAst);
                break;
            case "<MainFuncDef>":
                // Error b,g
                checkMainFuncDefChecker(rootAst);
                break;
            case "ASSIGN":
                // Error h
                checkAssignChecker(rootAst);
                break;
            case "<UnaryExp>":
                // Error b,c,d,e
                checkUnaryExpChecker(rootAst);
                break;
            case "PRINTFTK":
                // Error l
                checkPrintfChecker(rootAst);
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
        AstNode rootAst = sonAst.getParent();
        for (AstNode child : rootAst.getChildList()) {
            if (child.equals(sonAst)) {
                continue;
            }
            SemanticAnalysis.getRootChecker().check(child);
        }
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

    private void checkReturnStmtChecker(AstNode sonAst) throws IOException {
        AstNode rootAst = sonAst.getParent();
        if (rootAst.getChildList().size() >= 2 &&
                rootAst.getChildList().get(1).getGrammarType().equals("<Exp>")) {
            FuncSymbol func = SymbolTable.getCurrentFunc();
            if (func.getReturnType().equals(Symbol.SymType.VOID)) {
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
        checkFuncDefSubChecker(rootAst, symbol);
    }

    private void checkFuncDefSubChecker(AstNode rootAst, Symbol symbol) throws IOException {
        if (!SymbolTable.addSymbol(symbol)) {
            ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getStartLine()));
        }
        SymbolTable.setCurrentFunc((FuncSymbol) symbol);
        SymbolTable.createStackSymbolTable();
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<Block>")) {
                SymDefiner.setParamInfo(rootAst, (FuncSymbol) symbol);
            }
            SemanticAnalysis.preTraverse(astNode);
        }
        SymbolTable.destroyStackSymbolTable();
        AstNode block = rootAst.getChildList().get(rootAst.getChildList().size() - 1);
        int senNum = block.getChildList().size();
        if (symbol.getSymbolType() != Symbol.SymType.VOID) {
            AstNode lastSentence = block.getChildList().get(senNum - 2);
            if (lastSentence.isLeaf() || !(lastSentence.getChildList().get(0).
                    getChildList().get(0).getGrammarType().equals("RETURNTK") &&
                    lastSentence.getChildList().get(0).getChildList().size() >= 2 &&
                    lastSentence.getChildList().get(0).getChildList().get(1)
                            .getGrammarType().equals("<Exp>"))) {
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
    private void checkMainFuncDefChecker(AstNode rootAst) throws IOException {
        SymbolTable.setGlobalArea(false);
        Symbol symbol = semanticAnalysisChecker.createMainFuncDefChecker(rootAst);
        checkFuncDefSubChecker(rootAst, symbol);
    }

    //Lexer_part

    private void checkAssignChecker(AstNode sonAst) throws IOException {
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
                        if (symbol instanceof IntSymbol && tempDim.size() !=
                                ((IntSymbol) symbol).getDim()) {
                            tempDim.add(SymCalc.calc(child));
                        }
                    }
                }
                if (symbol instanceof ConstSymbol) {
                    ErrorController.addError(new ErrorToken("h", rootAst.getSpan().getEndLine()));
                }
            } else if (astNode.getGrammarType().equals("<Exp>")) {
                if (OutputController.getIsCalcMode()) {
                    if (symbol instanceof IntSymbol) {
                        int value = SymCalc.calc(astNode);
                        if (tempDim.isEmpty()) {
                            ((IntSymbol) symbol).updateValue(value);
                        } else if (tempDim.size() == 1) {
                            ((IntSymbol) symbol).updateValue(value, tempDim.get(0));
                        } else {
                            ((IntSymbol) symbol).updateValue(value, tempDim.get(0), tempDim.get(1));
                        }
                    }
                }
            }
        }
        SemanticAnalysis.preTraverse(sonAst);
    }

    private void checkUnaryExpChecker(AstNode rootAst) throws IOException {
        if (rootAst.getChildList().size() >= 3 && rootAst.getChildList().get(1).
                getGrammarType().equals("LPARENT")) {
            Symbol symbol = SymbolTable.getSymByName(rootAst.getChildList().get(0)
                    .getSymToken().getWord());
            if ((!(symbol instanceof FuncSymbol) && symbol != null)) {
                ErrorController.addError(new ErrorToken("b", rootAst.getSpan().getEndLine()));
            } else if (symbol == null) {
                ErrorController.addError(new ErrorToken("c", rootAst.getSpan().getEndLine()));
            } else {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;
                int paramNum = funcSymbol.getParamNum();
                ArrayList<AstNode> childList = new ArrayList<>();
                for (AstNode astNode : rootAst.getChildList().get(2).getChildList()) {
                    if (astNode.getGrammarType().equals("<Exp>")) {
                        childList.add(astNode);
                    }
                }
                if (paramNum != childList.size()) {
                    ErrorController.addError(new ErrorToken("d", rootAst.getSpan().getStartLine()));
                } else {
                    for (int i = 0; i < paramNum; i++) {
                        Symbol.SymType paramType = funcSymbol.getFuncParamTypes().get(i);
                        Symbol.SymType argType = SymTypeJudge.getExpType(childList.get(i));
                        Integer paramdim = funcSymbol.getFuncParamDims().get(i);
                        Integer argdim = SymDefiner.getExpDim(childList.get(i));
                        if (paramType.equals(Symbol.SymType.VOID) ||
                                argType.equals(Symbol.SymType.VOID) || !paramdim.equals(argdim)) {
                            ErrorController.addError(new ErrorToken("e",
                                    rootAst.getSpan().getStartLine()));
                        }
                    }
                }
            }
        }
        SemanticAnalysis.preTraverse(rootAst);
    }

    private void checkPrintfChecker(AstNode astNode) throws IOException {
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
