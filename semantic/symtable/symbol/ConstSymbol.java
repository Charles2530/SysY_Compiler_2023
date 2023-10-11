package semantic.symtable.symbol;

import semantic.symtable.Symbol;

import java.util.ArrayList;

public class ConstSymbol extends Symbol {
    private final int dim;
    private final ArrayList<Integer> initValue;
    private final ArrayList<Integer> space;

    public ConstSymbol(String symbolName, SymType symbolType, int dim,
                       ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType);
        this.dim = dim;
        this.initValue = initValue;
        this.space = space;
        // array init
        if (dim > 0) {
            int size = 1;
            for (int i = 0; i < dim; i++) {
                size *= space.get(i);
            }
            for (int i = 0; i < size - initValue.size(); i++) {
                this.initValue.add(0);
            }
        }
    }

    public int getDim() {
        return dim;
    }

    public Integer getConstValue(int... idx) {
        if (initValue.isEmpty()) {
            return null;
        }
        if (idx.length == 0) {
            return initValue.get(0);
        } else if (idx.length == 1) {
            return initValue.get(idx[0]);
        } else {
            if (idx[0] == 0) {
                return initValue.get(idx[1]);
            }
            return initValue.get(idx[0] * space.get(0) + idx[1]);
        }
    }
}
