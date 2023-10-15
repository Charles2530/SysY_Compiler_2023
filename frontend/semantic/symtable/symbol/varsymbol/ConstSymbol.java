package frontend.semantic.symtable.symbol.varsymbol;

import frontend.semantic.symtable.symbol.VarSymbol;

import java.util.ArrayList;

public class ConstSymbol extends VarSymbol {

    public ConstSymbol(String symbolName, SymType symbolType, int dim,
                       ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType, dim, initValue, space);
    }
}
