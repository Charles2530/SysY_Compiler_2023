package frontend.generation.semantic.symtable.symbol;

import midend.generation.value.construction.user.Function;
import frontend.generation.semantic.symtable.Symbol;

import java.util.ArrayList;

/**
 * FuncSymbol 是符号表中的函数表项,继承于Symbol
 */
public class FuncSymbol extends Symbol {
    /**
     * funcParamTypes 是函数的参数类型列表
     * funcParamDims 是函数的参数维度列表
     * returnType 是函数的返回值类型
     * function 是函数的LLVM IR表示,用于生成中间代码时使用
     */
    private ArrayList<SymType> funcParamTypes;
    private ArrayList<Integer> funcParamDims;
    private final SymType returnType;
    private Function function;

    public FuncSymbol(String symbolName, SymType symbolType) {
        super(symbolName, symbolType);
        this.returnType = symbolType;
    }

    /**
     * setParamInfo 是用于设置函数表项的参数信息
     */
    public void setParamInfo(ArrayList<SymType> funcParamTypes,
                             ArrayList<Integer> funcParamDims) {
        this.funcParamTypes = funcParamTypes;
        this.funcParamDims = funcParamDims;
    }

    public SymType getReturnType() {
        return returnType;
    }

    public int getParamNum() {
        return funcParamTypes.size();
    }

    public ArrayList<SymType> getFuncParamTypes() {
        return funcParamTypes;
    }

    public ArrayList<Integer> getFuncParamDims() {
        return funcParamDims;
    }

    public Function getfunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
