package semantic.utils;

import semantic.symtable.Symbol;
import semantic.symtable.SymbolTable;
import semantic.symtable.symbol.ConstSymbol;
import semantic.symtable.symbol.FuncSymbol;
import semantic.symtable.symbol.VarSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class SymDefiner {
    public static void setParamInfo(AstNode astNode, FuncSymbol symbol) {
        ArrayList<Symbol.SymType> fparamTypes = new ArrayList<>();
        ArrayList<Integer> fparamDims = new ArrayList<>();
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
                    fparamTypes.add(type);
                    fparamDims.add(dim);
                }
            }
        }
        symbol.setParamInfo(fparamTypes, fparamDims);
    }

    public static Integer getExpDim(AstNode astNode) {
        return switch (astNode.getGrammarType()) {
            case "<Number>" -> getExpDimNumber();
            case "<LVal>" -> getExpDimLVal(astNode);
            case "<UnaryExp>" -> getExpDimUnaryExp(astNode);
            case "<PrimaryExp>" -> getExpDimPrimaryExp(astNode);
            default -> getExpDim(astNode.getChildList().get(0));
        };
    }

    private static Integer getExpDimNumber() {
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