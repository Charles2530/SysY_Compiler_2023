package semantic.symbolTable.symbol;

import semantic.symbolTable.Symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymType> FParamTypes;
    private ArrayList<Integer> FParamDims;
    private SymType returnType;

    public FuncSymbol(String symbolName, SymType symbolType) {
        super(symbolName, symbolType);
    }

    public void setParamInfo(ArrayList<SymType> fParamTypes, ArrayList<Integer> fParamDims) {
        this.FParamTypes = fParamTypes;
        this.FParamDims = fParamDims;
    }

    public SymType getReturnType() {
        return returnType;
    }
}
