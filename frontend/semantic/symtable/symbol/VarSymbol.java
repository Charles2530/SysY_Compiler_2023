package frontend.semantic.symtable.symbol;

import frontend.semantic.symtable.Symbol;
import midend.generation.utils.IrType;
import midend.generation.utils.irtype.ArrayType;
import midend.generation.utils.irtype.VarType;
import midend.generation.value.Value;
import midend.generation.value.construction.procedure.Initial;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    protected final int dim;
    protected final ArrayList<Integer> initValue;
    protected final ArrayList<Integer> space;
    protected Integer spaceTot;

    protected Value value;

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
        IrType type = (dim == 0) ? new VarType(32) : new ArrayType(spaceTot, new VarType(32));
        return new Initial(type, initValue);
    }
}
