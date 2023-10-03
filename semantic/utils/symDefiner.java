package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.symbol.FuncSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class symDefiner {
    public static void setParamInfo(AstNode astNode, FuncSymbol symbol) {
        ArrayList<Symbol.SymType> FParamTypes = new ArrayList<>();
        ArrayList<Integer> FParamDims = new ArrayList<>();
        if (astNode.getChildList().get(3).getGrammarType().equals("<FuncFormalParams>")) {
            AstNode funcFormalParams = astNode.getChildList().get(3);
            for (AstNode child : funcFormalParams.getChildList()) {
                if (child.getGrammarType().equals("<FuncFParam>")) {
                    FParamTypes.add(Symbol.SymType.valueOf(child.getChildList().get(0).getSymToken().getWord()));
                    FParamDims.add(child.getChildList().size() - 1);
                }
            }
        }
        symbol.setParamInfo(FParamTypes, FParamDims);
    }
}
