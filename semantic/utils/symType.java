package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.FuncSymbol;
import syntax.AstNode;

import java.util.Objects;

public class symType {
    public static Symbol.SymType getExpType(AstNode astNode) {
        switch (astNode.getGrammarType()) {
            case "<Number>":
                return getExpTypeNumber(astNode);
            case "<LVal>":
                return getExpTypeLVal(astNode);
            case "<UnaryExp>":
                return getExpTypeUnaryExp(astNode);
            case "<PrimaryExp>":
                return getExpTypePrimaryExp(astNode);
            default:
                return getExpType(astNode.getChildList().get(0));
        }
    }

    private static Symbol.SymType getExpTypeLVal(AstNode astNode) {
        return Objects.requireNonNull(SymbolTable.getSymByName(astNode.getChildList().get(0)
                .getSymToken().getWord())).getSymbolType();
    }

    private static Symbol.SymType getExpTypeNumber(AstNode astNode) {
        if (astNode.getChildList().get(0).getGrammarType().equals("INTCON")) {
            return Symbol.SymType.INT;
        } else {
            return Symbol.SymType.VOID;
        }
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
