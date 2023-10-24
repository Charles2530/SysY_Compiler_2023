package frontend.semantic;

import frontend.semantic.symtable.Symbol;
import frontend.semantic.symtable.SymbolTable;
import frontend.semantic.symtable.symbol.varsymbol.ConstSymbol;
import frontend.semantic.symtable.symbol.FuncSymbol;
import frontend.semantic.symtable.symbol.varsymbol.IntSymbol;
import frontend.semantic.utils.SymCalc;
import frontend.syntax.AstNode;

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
                space.add(SymCalc.calc(astNode));
            } else if (astNode.getGrammarType().equals("<ConstInitVal>")) {
                initValAst = astNode;
            }
        }
        ArrayList<Integer> initValue = new ArrayList<>();
        if (initValAst != null) {
            initValue = SymCalc.calcConstInitVal(dim, initValAst);
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
                space.add(SymCalc.calc(astNode));
            }
            if (astNode.getGrammarType().equals("<InitVal>")) {
                initValAst = astNode;
            }
        }
        Symbol.SymType symbolType = Symbol.SymType.INT;
        ArrayList<Integer> initValue = new ArrayList<>();
        if (initValAst != null && SymbolTable.isIsGlobalArea()) {
            initValue = SymCalc.calcInitVal(dim, initValAst);
        }
        return new IntSymbol(symbolName, symbolType, dim, initValue, space);
    }

    public Symbol createFuncFParamChecker(AstNode rootAst) {
        String symbolName = rootAst.getChildList().get(1).getSymToken().getWord();
        int dim = 0;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < rootAst.getChildList().size(); i++) {
            if (rootAst.getChildList().get(i).getGrammarType().equals("LBRACK")) {
                dim++;
                if (i + 1 < rootAst.getChildList().size()) {
                    if (rootAst.getChildList().get(i + 1).getGrammarType().equals("<ConstExp>")) {
                        list.add(SymCalc.calc(rootAst.getChildList().get(i + 1)));
                    } else {
                        list.add(-1);
                    }
                }
            }
        }
        AstNode bytType = rootAst.getChildList().get(0);
        Symbol.SymType symbolType = bytType.getChildList().get(0).getGrammarType().equals("INTTK") ?
                Symbol.SymType.INT : Symbol.SymType.VOID;
        return new IntSymbol(symbolName, symbolType, dim, new ArrayList<>(), list);
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
