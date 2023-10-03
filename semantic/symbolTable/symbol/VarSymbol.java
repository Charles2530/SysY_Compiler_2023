package semantic.symbolTable.symbol;

import semantic.symbolTable.Symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private int dim;
    private ArrayList<Integer> initValue;
    private int space;

    public VarSymbol(String symbolName, SymType symbolType, int dim,
                     ArrayList<Integer> initValue, int space) {
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
            return initValue.get(0);
        } else if (idx.length == 1) {
            return initValue.get(idx[0]);
        } else {
            if (idx[0] == 0) {
                return initValue.get(idx[1]);
            }
            return initValue.get(idx[0] * space + idx[1]);
        }
    }
}
