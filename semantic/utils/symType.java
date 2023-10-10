package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import syntax.AstNode;

import java.util.Objects;

public class symType {
    public static Symbol.SymType getExpType(AstNode astNode) {
        switch (astNode.getGrammarType()) {
            case "<Number>":
                return getExpTypeNumber(astNode);
            case "<LVal>":
                return getExpTypeLVal(astNode);
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

}
