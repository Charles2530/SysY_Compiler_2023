package frontend.semantic.symtable.symbol;

import midend.generation.value.Value;
import midend.generation.value.construction.procedure.Initial;
import frontend.semantic.symtable.Symbol;

import java.util.ArrayList;

public class ConstSymbol extends Symbol {
    private final int dim;
    private final ArrayList<Integer> initValue;
    private final ArrayList<Integer> space;
    private Integer spaceTot;

    private Value value;

    public ConstSymbol(String symbolName, SymType symbolType, int dim,
                       ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType);
        this.dim = dim;
        this.initValue = initValue;
        this.space = space;
        this.spaceTot = 1;
        // array init
        if (dim > 0) {
            int size = 1;
            for (int i = 0; i < dim; i++) {
                size *= space.get(i);
            }
            this.spaceTot = size;
            for (int i = 0; i < size - initValue.size(); i++) {
                this.initValue.add(0);
            }
        }
    }

    public Integer getDim() {
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public ArrayList<Integer> getSpace() {
        return space;
    }

    public Integer getSpaceTot() {
        return spaceTot;
    }

    public Initial getInitial() {
        return new Initial(value.getType(), initValue);
    }
}
