package frontend.generation.semantic.symtable.symbol.varsymbol;

import frontend.generation.semantic.symtable.symbol.VarSymbol;

import java.util.ArrayList;

public class IntSymbol extends VarSymbol {

    public IntSymbol(String symbolName, SymType symbolType, int dim,
                     ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType, dim, initValue, space);
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
}
