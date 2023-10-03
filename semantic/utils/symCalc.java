package semantic.utils;

import semantic.symbolTable.Symbol;
import semantic.symbolTable.SymbolTable;
import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.VarSymbol;
import syntax.AstNode;

import java.util.ArrayList;

public class symCalc {
    public static ArrayList<Integer> calcInitVal(int dim, AstNode astNode) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (dim == 0) {
            ans.add(calc(astNode.getChildList().get(0)));
        } else {
            for (AstNode child : astNode.getChildList()) {
                if (child.getGrammarType().equals("<InitVal>")) {
                    ans.addAll(calcInitVal(dim - 1, child));
                }
            }
        }
        return ans;
    }

    public static int calc(AstNode astNode) {
        switch (astNode.getGrammarType()) {
            case "<Exp>", "<ConstExp>":
                return calc(astNode.getChildList().get(0));
            case "<AddExp>":
                return calcAddExp(astNode);
            case "<MulExp>":
                return calcMulExp(astNode);
            case "<UnaryExp>":
                return calcUnaryExp(astNode);
            case "<PrimaryExp>":
                return calcPrimaryExp(astNode);
            case "<Number>":
                return calcNumber(astNode);
            case "<LValExp>":
                return calcLValExp(astNode);
            default:
                return 0;
        }
    }

    private static int calcAddExp(AstNode astNode) {
        int ans = calc(astNode.getChildList().get(0));
        for (int i = 1; i < astNode.getChildList().size(); i++) {
            if (astNode.getChildList().get(i).getGrammarType().equals("PLUS")) {
                ans += calc(astNode.getChildList().get(++i));
            } else if (astNode.getChildList().get(i).getGrammarType().equals("MINU")) {
                ans -= calc(astNode.getChildList().get(++i));
            }
        }
        return ans;
    }

    private static int calcMulExp(AstNode astNode) {
        int ans = calc(astNode.getChildList().get(0));
        for (int i = 1; i < astNode.getChildList().size(); i++) {
            switch (astNode.getChildList().get(i).getGrammarType()) {
                case "MUL" -> ans *= calc(astNode.getChildList().get(++i));
                case "DIV" -> ans /= calc(astNode.getChildList().get(++i));
                case "MOD" -> ans %= calc(astNode.getChildList().get(++i));
            }
        }
        return ans;
    }

    private static int calcUnaryExp(AstNode astNode) {
        int ans = 0;
        AstNode child = astNode.getChildList().get(0);
        if (child.getGrammarType().equals("<UnaryOp>")) {
            if (child.getChildList().get(0).getGrammarType().equals("PLUS")) {
                ans = calc(child.getChildList().get(1));
            } else if (child.getChildList().get(0).getGrammarType().equals("MINU")) {
                ans = -calc(child.getChildList().get(1));
            } else if (child.getChildList().get(0).getGrammarType().equals("NOT")) {
                ans = calc(child.getChildList().get(1)) == 0 ? 1 : 0;
            }
        } else if (child.getGrammarType().equals("<PrimaryExp>")) {
            ans = calc(child);
        }
        return ans;
    }

    private static int calcPrimaryExp(AstNode astNode) {
        int ans;
        switch (astNode.getChildList().get(0).getGrammarType()) {
            case "<Number>", "<LValExp>" -> ans = calc(astNode.getChildList().get(0));
            default -> ans = calc(astNode.getChildList().get(1));
        }
        return ans;
    }

    private static int calcNumber(AstNode astNode) {
        return Integer.parseInt(astNode.getChildList().get(0).getSymToken().getWord());
    }

    private static int calcLValExp(AstNode astNode) {
        Symbol symbol = SymbolTable.getSymByName(astNode.getChildList().get(0)
                .getSymToken().getWord());
        if (symbol == null) {
            return 0;
        }
        ArrayList<Integer> bracketVal = new ArrayList<>();
        for (int i = 1; i < astNode.getChildList().size(); i++) {
            if (astNode.getChildList().get(i).getGrammarType().equals("<Exp>")) {
                bracketVal.add(calc(astNode.getChildList().get(i)));
            }
        }
        if (symbol instanceof ConstSymbol) {
            ConstSymbol constSymbol = (ConstSymbol) symbol;
            if (constSymbol.getDim() == 0) {
                return constSymbol.getConstValue();
            } else if (constSymbol.getDim() == 1) {
                return constSymbol.getConstValue(bracketVal.get(0));
            }
            return constSymbol.getConstValue(bracketVal.get(0), bracketVal.get(1));
        } else {
            VarSymbol varSymbol = (VarSymbol) symbol;
            if (varSymbol.getDim() == 0) {
                return varSymbol.getConstValue();
            } else if (varSymbol.getDim() == 1) {
                return varSymbol.getConstValue(bracketVal.get(0));
            }
            return varSymbol.getConstValue(bracketVal.get(0), bracketVal.get(1));
        }
    }
}
