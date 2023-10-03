package semantic.symbolTable.symbol;

import semantic.symbolTable.Symbol;

public class VarSymbol extends Symbol {
    private int dim;
    private Symbol initValue;
    private int space;

    public VarSymbol(String symbolName, SymType symbolType, int dim, Symbol initValue, int space) {
        super(symbolName, symbolType);
        this.dim = dim;
        this.initValue = initValue;
        this.space = space;
    }

    public boolean Isglobal() {
        return super.getSymbolLevel() == 0;
    }

    public int getDim() {
        return dim;
    }

    public int getConstValue(int... idx) {
        if (idx.length == 0) {
//            return initValue.getConstValue();
        } else if (idx.length == 1) {
//            return initValue.getConstValue(idx[0]);
        } else {
//            return initValue.getConstValue(idx[0], idx[1]);
        }
        return 0;
    }
}
