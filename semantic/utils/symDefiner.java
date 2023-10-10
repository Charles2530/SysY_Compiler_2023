package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import semantic.symbolTable.symbol.VarSymbol;
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

    public static Integer getExpDim(AstNode astNode) {
        switch (astNode.getGrammarType()) {
            case "<Number>":
                return getExpDimNumber(astNode);
            case "<LVal>":
                return getExpDimLVal(astNode);
            case "<UnaryExp>":
                return getExpDimUnaryExp(astNode);
            case "<PrimaryExp>":
                return getExpDimPrimaryExp(astNode);
            default:
                return getExpDim(astNode.getChildList().get(0));
        }
    }

    private static Integer getExpDimNumber(AstNode astNode) {
        return 0;
    }

    private static Integer getExpDimLVal(AstNode astNode) {
        Symbol symbol = SymbolTable.getSymByName(astNode.getChildList().get(0)
                .getSymToken().getWord());
        int reduceDim = 0;
        for (AstNode child : astNode.getChildList()) {
            if (child.getGrammarType().equals("LBRACK")) {
                reduceDim++;
            }
        }
        if (symbol instanceof VarSymbol) {
            return ((VarSymbol) symbol).getDim() - reduceDim;
        } else if (symbol instanceof ConstSymbol) {
            return ((ConstSymbol) symbol).getDim() - reduceDim;
        } else {
            return 0;
        }
    }

    private static Integer getExpDimUnaryExp(AstNode astNode) {
        if (astNode.getChildList().size() == 1) {
            return getExpDim(astNode.getChildList().get(0));
        } else {
            return 0;
        }
    }

    private static Integer getExpDimPrimaryExp(AstNode astNode) {
        if (astNode.getChildList().size() == 1) {
            return getExpDim(astNode.getChildList().get(0));
        } else {
            return getExpDim(astNode.getChildList().get(1));
        }
    }
}
