package frontend.semantic.symtable;

public class Symbol {

    public enum SymType {
        INT, VOID, CONST
    }

    private final String symbolName;
    private final SymType symbolType;
    private final Integer symbolLevel;

    public Symbol(String symbolName, SymType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
        this.symbolLevel = SymbolTable.getCurlevel();
    }

    public String getSymbolName() {
        return symbolName;
    }

    public SymType getSymbolType() {
        return symbolType;
    }

    public Integer getSymbolLevel() {
        return symbolLevel;
    }

}
