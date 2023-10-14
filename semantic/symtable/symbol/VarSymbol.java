package semantic.symtable.symbol;

import generation.utils.irtype.VarType;
import generation.value.Value;
import generation.value.construction.procedure.Initial;
import semantic.symtable.Symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private final int dim;
    private final ArrayList<Integer> initValue;
    private final ArrayList<Integer> space;
    private Integer spaceTot;
    private Value value;

    public VarSymbol(String symbolName, SymType symbolType, int dim,
                     ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType);
        this.dim = dim;
        this.initValue = (initValue == null) ? new ArrayList<>() : initValue;
        this.space = space;
        this.spaceTot = 1;
        // array init
        if (dim > 0) {
            int size = 1;
            for (Integer integer : space) {
                size *= integer;
            }
            this.spaceTot = size;
            for (int i = 0; i < size - this.initValue.size(); i++) {
                this.initValue.add(0);
            }
        }
    }

    public Integer getDim() {
        return dim;
    }

    public Integer getConstValue(int... idx) {
        if (initValue == null || initValue.isEmpty()) {
            return 0;
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

    public void updateValue(int val, int... idx) {
        if (idx.length == 0) {
            initValue.set(0, val);
        } else if (idx.length == 1) {
            initValue.set(idx[0], val);
        } else {
            if (idx[0] == 0) {
                initValue.set(idx[1], val);
            }
            initValue.set(idx[0] * space.get(0) + idx[1], val);
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

    /*TODO:change later*/
    public Initial getInitial() {
        return new Initial(new VarType(32), initValue);
    }
}
