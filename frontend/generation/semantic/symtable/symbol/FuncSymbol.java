package frontend.generation.semantic.symtable.symbol;

import midend.generation.value.construction.user.Function;
import frontend.generation.semantic.symtable.Symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymType> fparamTypes;
    private ArrayList<Integer> fparamDims;
    private final SymType returnType;

    private Function function;

    public FuncSymbol(String symbolName, SymType symbolType) {
        super(symbolName, symbolType);
        this.returnType = symbolType;
    }

    public void setParamInfo(ArrayList<SymType> fparamTypes,
                             ArrayList<Integer> fparamDims) {
        this.fparamTypes = fparamTypes;
        this.fparamDims = fparamDims;
    }

    public SymType getReturnType() {
        return returnType;
    }

    public int getParamNum() {
        return fparamTypes.size();
    }

    public ArrayList<SymType> getFparamTypes() {
        return fparamTypes;
    }

    public ArrayList<Integer> getFparamDims() {
        return fparamDims;
    }

    public Function getfunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
