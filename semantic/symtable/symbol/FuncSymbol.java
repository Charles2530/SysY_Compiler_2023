package semantic.symtable.symbol;

import semantic.symtable.Symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymType> fparamTypes;
    private ArrayList<Integer> fparamDims;
    private final SymType returnType;

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
}
