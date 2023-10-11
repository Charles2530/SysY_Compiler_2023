package semantic.symtable;

import java.util.HashMap;

public class StackSymbolTable {
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
