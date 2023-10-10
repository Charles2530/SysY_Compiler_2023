package semantic;

import generation.utils.OutputController;
import semantic.symbolTable.Symbol;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import semantic.symbolTable.symbol.VarSymbol;
import semantic.utils.symCalc;
import syntax.AstNode;

import java.util.ArrayList;

public class SemanticAnalysisChecker {

    public Symbol createConstDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        int dim = 0;
        ArrayList<Integer> space = new ArrayList<>();
        AstNode initValAst = null;
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<ConstExp>")) {
                dim++;
                space.add(symCalc.calc(astNode));
            } else if (astNode.getGrammarType().equals("<ConstInitVal>")) {
                initValAst = astNode;
            }
        }
        ArrayList<Integer> initValue = new ArrayList<>();
        if (initValAst != null && OutputController.getIsCalcMode()) {
            initValue = symCalc.calcConstInitVal(dim, initValAst);
        }
        Symbol.SymType symbolType = Symbol.SymType.CONST;
        return new ConstSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public Symbol createVarDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(0).getSymToken().getWord();
        int dim = 0;
        ArrayList<Integer> space = new ArrayList<>();
        AstNode initValAst = null;
        for (AstNode astNode : rootAst.getChildList()) {
            if (astNode.getGrammarType().equals("<ConstExp>")) {
                dim++;
                space.add(symCalc.calc(astNode));
            }
            if (astNode.getGrammarType().equals("<InitVal>")) {
                initValAst = astNode;
            }
        }
        Symbol.SymType symbolType = Symbol.SymType.INT;
        ArrayList<Integer> initValue = new ArrayList<>();
        if (initValAst != null && OutputController.getIsCalcMode()) {
            initValue = symCalc.calcInitVal(dim, initValAst);
        }
        return new VarSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public Symbol createFuncFParamChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
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
        AstNode bytType = rootAst.getChildList().get(0);
        Symbol.SymType symbolType = bytType.getChildList().get(0).getGrammarType().equals("INTTK") ?
                Symbol.SymType.INT : Symbol.SymType.VOID;
        return new VarSymbol(symbolName, symbolType, dim, new ArrayList<>(), list);
    }

    public Symbol createMainFuncDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        Symbol.SymType returnType = Symbol.SymType.INT;
        return new FuncSymbol(symbolName, returnType);
    }

    public Symbol createFuncDefChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        Symbol.SymType returnType = (rootAst.getChildList().get(0).
                getChildList().get(0).getGrammarType()
                .equals("INTTK")) ? Symbol.SymType.INT : Symbol.SymType.VOID;
        return new FuncSymbol(symbolName, returnType);
    }
}
