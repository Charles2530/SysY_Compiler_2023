package frontend.generation.semantic.symtable;

import java.util.HashMap;

/**
 * StackSymbolTable 是一层栈式符号表
 */
public class StackSymbolTable {
    /**
     * symbols 是符号表项的HashMap,用于存储处于当前栈式符号表的符号表项
     */
    private final HashMap<String, Symbol> symbols;

    public StackSymbolTable() {
        this.symbols = new HashMap<>();
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getSymbolName(), symbol);
    }

    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    public HashMap<String, Symbol> getSymbols() {
        return symbols;
    }
}
