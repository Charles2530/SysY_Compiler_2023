package semantic.symbolTable;

import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private Stack<StackSymbolTable> symbolTables;
    private HashMap<String, Stack<StackSymbolTable>> symbolNameTables;

    public SymbolTable() {
        this.symbolTables = new Stack<>();
        this.symbolNameTables = new HashMap<>();
    }

    public boolean addSymbol(Symbol symbol) {
        StackSymbolTable topTable = this.symbolTables.peek();
        if (topTable.getSymbol(symbol.getSymbolName()) != null) {
            return false;
        }
        topTable.addSymbol(symbol);
        symbolNameTables.compute(symbol.getSymbolName(), (k, v) -> {
            if (v == null) {
                v = new Stack<>();
            }
            v.add(topTable);
            return v;
        });
        return true;
    }
}
