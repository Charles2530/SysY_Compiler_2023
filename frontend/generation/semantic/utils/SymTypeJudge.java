package frontend.generation.semantic.utils;

import frontend.generation.semantic.symtable.Symbol;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.FuncSymbol;
import frontend.generation.syntax.AstNode;

public class SymTypeJudge {
    public static Symbol.SymType getExpType(AstNode astNode) {
        return switch (astNode.getGrammarType()) {
            case "<Number>" -> getExpTypeNumber(astNode);
            case "<LVal>" -> getExpTypeLVal(astNode);
            case "<UnaryExp>" -> getExpTypeUnaryExp(astNode);
            case "<PrimaryExp>" -> getExpTypePrimaryExp(astNode);
            default -> getExpType(astNode.getChildList().get(0));
        };
    }

    private static Symbol.SymType getExpTypeLVal(AstNode astNode) {
        Symbol symbol = SymbolTable.getSymByName(astNode.getChildList().get(0)
                .getSymToken().getWord());
        return symbol == null ? Symbol.SymType.INT : symbol.getSymbolType();
    }

    private static Symbol.SymType getExpTypeNumber(AstNode astNode) {
        return astNode.getChildList().get(0).getGrammarType().equals("INTCON") ?
                Symbol.SymType.INT : Symbol.SymType.VOID;
    }

    private static Symbol.SymType getExpTypeUnaryExp(AstNode astNode) {
        if (astNode.getChildList().size() == 1) {
            return getExpType(astNode.getChildList().get(0));
        } else {
            if (astNode.getChildList().get(0).getGrammarType().equals("IDENFR")) {
                Symbol symbol = SymbolTable.getSymByName(
                        astNode.getChildList().get(0).getSymToken().getWord());
                if (symbol instanceof FuncSymbol) {
                    return ((FuncSymbol) symbol).getReturnType();
                } else {
                    return symbol.getSymbolType();
                }
            } else if (astNode.getChildList().get(0).getGrammarType().equals("<UnaryOp>")) {
                return getExpType(astNode.getChildList().get(1));
            } else {
                return Symbol.SymType.INT;
            }
        }
    }

    private static Symbol.SymType getExpTypePrimaryExp(AstNode astNode) {
        if (astNode.getChildList().size() == 1) {
            return getExpType(astNode.getChildList().get(0));
        } else {
            return getExpType(astNode.getChildList().get(1));
        }
    }
}
