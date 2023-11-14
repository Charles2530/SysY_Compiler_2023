package frontend.generation.semantic.utils;

import frontend.generation.semantic.symtable.Symbol;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.semantic.symtable.symbol.FuncSymbol;
import frontend.generation.semantic.symtable.symbol.VarSymbol;
import frontend.generation.syntax.AstNode;
import midend.generation.llvm.LLvmGenIR;
import midend.generation.value.Value;

import java.util.ArrayList;

/**
 * SymDefiner 是语义分析用于定义符号表表项的工具类
 */
public class SymDefiner {
    /**
     * llvmGenIR 是用于生成LLVM IR类型中间代码的生成器,这里引入
     * 是由于在生成中间代码时需要重建符号表，引入方便快速获得符号
     * 表的对应表项
     */
    private static LLvmGenIR llvmGenIR = new LLvmGenIR();

    /**
     * setParamInfo 是用于设置函数表项的参数信息的函数,即获取函数的参数类型和参数维度
     */
    public static void setParamInfo(AstNode astNode, FuncSymbol symbol) {
        ArrayList<Symbol.SymType> funcParamTypes = new ArrayList<>();
        ArrayList<Integer> funcParamDims = new ArrayList<>();
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
                    funcParamTypes.add(type);
                    funcParamDims.add(dim);
                }
            }
        }
        symbol.setParamInfo(funcParamTypes, funcParamDims);
    }

    /**
     * getExpDim 采用模拟递归下降的方式获得对应Exp的维数信息
     * ，下方定义其子函数用于递归下降的进行
     */
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
        if (symbol instanceof VarSymbol varSymbol) {
            return varSymbol.getDim() - reduceDim;
        }
        return 0;
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

    public static ArrayList<Value> genIrValues(AstNode rootAst, int dim) {
        ArrayList<Value> ans = new ArrayList<>();
        if (dim == 0) {
            Value value = llvmGenIR.genIrAnalysis(rootAst.getChildList().get(0));
            ans.add(value);
        } else {
            for (AstNode child : rootAst.getChildList()) {
                if (child.getGrammarType().equals("<InitVal>")) {
                    ArrayList<Value> temp = genIrValues(child, dim - 1);
                    ans.addAll(temp);
                }
            }
        }
        return ans;
    }

    public static ArrayList<Value> genIrConstValues(AstNode rootAst, int dim) {
        ArrayList<Value> ans = new ArrayList<>();
        if (dim == 0) {
            Value value = llvmGenIR.genIrAnalysis(rootAst.getChildList().get(0));
            ans.add(value);
        } else {
            for (AstNode child : rootAst.getChildList()) {
                if (child.getGrammarType().equals("<ConstInitVal>")) {
                    ArrayList<Value> temp = genIrConstValues(child, dim - 1);
                    ans.addAll(temp);
                }
            }
        }
        return ans;
    }
}
