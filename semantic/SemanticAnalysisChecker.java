package semantic;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import semantic.symbolTable.symbol.VarSymbol;
import semantic.utils.symCalc;
import syntax.AstNode;

import java.util.ArrayList;

public class SemanticAnalysisChecker {
    private SymbolTable symbolTable;

    public SemanticAnalysisChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Symbol createConstDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        int dim = 0;
        int space = 1;
        AstNode initValAst = null;
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<ConstExp>")) {
                dim++;
                space *= symCalc.calc(astNode);
            }
            if (astNode.getGrammarType().equals("<ConstInitVal>")) {
                initValAst = astNode;
            }
        }
        ArrayList<Integer> initValue = null;
        if (initValAst != null) {
            initValue = symCalc.calcInitVal(dim, initValAst);
        }
        Symbol.SymType symbolType;
        if (dim == 0) {
            symbolType = Symbol.SymType.CONST;
        } else {
            symbolType = Symbol.SymType.ARRAY;
        }
        return new ConstSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public Symbol createVarDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        int dim = 0;
        Integer value = null;
        AstNode initValAst = null;
        int space = 1;
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<ConstExp>")) {
                dim++;
                space *= symCalc.calc(astNode);
            }
            if (astNode.getGrammarType().equals("<InitVal>")) {
                initValAst = astNode;
            }
        }
        Symbol.SymType symbolType;
        if (dim == 0) {
            symbolType = Symbol.SymType.INT;
        } else {
            symbolType = Symbol.SymType.ARRAY;
        }
        ArrayList<Integer> initValue = null;
        if (initValAst != null) {
            initValue = symCalc.calcInitVal(dim, initValAst);
        }
        return new VarSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public ArrayList<Integer> createInitValChecker(int dim, AstNode rootAst) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (dim == 0) {
            ans.add(symCalc.calc(rootAst));
        } else {
            for (AstNode astNode : rootAst.getChildList()) {
                if (astNode.getGrammarType().equals("<InitVal>")) {
                    ans.addAll(createInitValChecker(dim - 1, astNode));
                }
            }
        }
        return ans;
    }

    public Symbol createFuncFParamChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        Symbol.SymType symbolType;
        if (rootAst.getChildList().get(0).getGrammarType().equals("INTTK")) {
            symbolType = Symbol.SymType.INT;
        } else {
            symbolType = Symbol.SymType.VOID;
        }
        int dim = 0;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().equals("LBRACK")) {
                dim++;
                if (rootAst.getChildList().get(i + 1).getGrammarType().equals("ConstExp")) {
                    list.add(symCalc.calc(rootAst.getChildList().get(i + 1)));
                } else {
                    list.add(-1);
                }
            }
        }
        return new VarSymbol(symbolName, symbolType, dim, list, 1);
    }

    public Symbol createMainFuncDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        Symbol.SymType returnType = Symbol.SymType.INT;
        return new FuncSymbol(symbolName, returnType);
    }

    public Symbol createFuncDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        Symbol.SymType returnType = (rootAst.getChildList().get(0).getGrammarType()
                .equals("INTTK")) ? Symbol.SymType.INT : Symbol.SymType.VOID;
        return new FuncSymbol(symbolName, returnType);
    }
}
