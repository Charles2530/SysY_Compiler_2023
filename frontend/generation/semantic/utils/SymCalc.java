package frontend.generation.semantic.utils;

import frontend.generation.semantic.symtable.Symbol;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.VarSymbol;
import frontend.generation.syntax.AstNode;

import java.util.ArrayList;

/**
 * SymCalc 是语义分析用于计算表达式的工具类,主要可以将位于全局区
 * 的全局变量的值在编译时期进行计算,并将其值存入符号表中，同时也
 * 担任了部分常数表达式的化简工作
 */
public class SymCalc {
    /**
     * calcInitVal 用于存储分析得出的ValDef的初始化值，由维数分为
     * 处理变量和数组两种情况,故返回值为一个数组
     */
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

    /**
     * calcConstInitVal 用于存储分析得出的ConstDef的初始化值，由维数分为
     * 处理常量和常量数组两种情况,故返回值为一个数组
     */
    public static ArrayList<Integer> calcConstInitVal(int dim, AstNode astNode) {
        ArrayList<Integer> ans = new ArrayList<>();
        if (dim == 0) {
            ans.add(calc(astNode.getChildList().get(0)));
        } else {
            for (AstNode child : astNode.getChildList()) {
                if (child.getGrammarType().equals("<ConstInitVal>")) {
                    ans.addAll(calcConstInitVal(dim - 1, child));
                }
            }
        }
        return ans;
    }

    /**
     * calc 是计算表达式的主函数,下面定义了不同类型分别的处理函数
     */
    public static int calc(AstNode astNode) {
        return switch (astNode.getGrammarType()) {
            case "<Exp>", "<ConstExp>" -> calc(astNode.getChildList().get(0));
            case "<AddExp>" -> calcAddExp(astNode);
            case "<MulExp>" -> calcMulExp(astNode);
            case "<UnaryExp>" -> calcUnaryExp(astNode);
            case "<PrimaryExp>" -> calcPrimaryExp(astNode);
            case "<Number>" -> calcNumber(astNode);
            case "<LVal>" -> calcLValExp(astNode);
            default -> 0;
        };
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
                case "MULT" -> ans *= calc(astNode.getChildList().get(++i));
                case "DIV" -> ans /= calc(astNode.getChildList().get(++i));
                case "MOD" -> ans %= calc(astNode.getChildList().get(++i));
                default -> {
                }
            }
        }
        return ans;
    }

    private static int calcUnaryExp(AstNode astNode) {
        int ans = 0;
        AstNode child = astNode.getChildList().get(0);
        if (child.getGrammarType().equals("<UnaryOp>")) {
            ans = switch (child.getChildList().get(0).getGrammarType()) {
                case "PLUS" -> calc(astNode.getChildList().get(1));
                case "MINU" -> -calc(astNode.getChildList().get(1));
                case "NOT" -> calc(astNode.getChildList().get(1)) == 0 ? 1 : 0;
                default -> ans;
            };
        } else if (child.getGrammarType().equals("<PrimaryExp>")) {
            ans = calcPrimaryExp(child);
        }
        return ans;
    }

    private static int calcPrimaryExp(AstNode astNode) {
        int ans;
        switch (astNode.getChildList().get(0).getGrammarType()) {
            case "<Number>", "<LVal>" -> ans = calc(astNode.getChildList().get(0));
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
        if (symbol instanceof VarSymbol varSymbol) {
            if (varSymbol.getDim() == 0) {
                return varSymbol.getConstValue();
            } else if (varSymbol.getDim() == 1) {
                return varSymbol.getConstValue(bracketVal.get(0));
            }
            return varSymbol.getConstValue(bracketVal.get(0), bracketVal.get(1));
        }
        return 0;
    }
}
