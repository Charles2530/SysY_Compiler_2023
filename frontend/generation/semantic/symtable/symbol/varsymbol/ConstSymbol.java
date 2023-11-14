package frontend.generation.semantic.symtable.symbol.varsymbol;

import frontend.generation.semantic.symtable.symbol.VarSymbol;

import java.util.ArrayList;

/**
 * ConstSymbol 是符号表中的常量表项,继承于VarSymbol
 */
public class ConstSymbol extends VarSymbol {

    public ConstSymbol(String symbolName, SymType symbolType, int dim,
                       ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType, dim, initValue, space);
    }
}
