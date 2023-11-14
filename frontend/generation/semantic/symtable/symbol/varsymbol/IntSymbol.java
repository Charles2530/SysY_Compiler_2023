package frontend.generation.semantic.symtable.symbol.varsymbol;

import frontend.generation.semantic.symtable.symbol.VarSymbol;

import java.util.ArrayList;

/**
 * IntSymbol 是符号表中的整型变量表项,继承于VarSymbol
 */
public class IntSymbol extends VarSymbol {

    public IntSymbol(String symbolName, SymType symbolType, int dim,
                     ArrayList<Integer> initValue, ArrayList<Integer> space) {
        super(symbolName, symbolType, dim, initValue, space);
    }

    /**
     * updateValue 是用于更新全局区变量的初始化值的函数(后续基本不使用)
     */
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
