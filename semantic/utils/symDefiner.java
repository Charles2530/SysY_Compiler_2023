package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.symbol.FuncSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class symDefiner {
    public static void setParamInfo(AstNode astNode, FuncSymbol symbol) {
        ArrayList<Symbol.SymType> FParamTypes = new ArrayList<>();
        ArrayList<Integer> FParamDims = new ArrayList<>();
        if (astNode.getChildList().get(3).getGrammarType().equals("<FuncFParams>")) {
            AstNode funcFormalParams = astNode.getChildList().get(3);
            for (AstNode child : funcFormalParams.getChildList()) {
                if (child.getGrammarType().equals("<FuncFParam>")) {
                    int dim = 0;
                    for (AstNode obj : child.getChildList()) {
                        if (obj.getGrammarType().equals("LBRACK")) {
                            dim++;
                        }
                    }
                    Symbol.SymType type = child.getChildList().get(0)
                            .getChildList().get(0).getGrammarType().equals("INTTK") ?
                            Symbol.SymType.INT : Symbol.SymType.CONST;
                    FParamTypes.add(type);
                    FParamDims.add(dim);
                }
            }
        }
        symbol.setParamInfo(FParamTypes, FParamDims);
    }
}
