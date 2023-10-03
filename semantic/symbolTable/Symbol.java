package semantic.symbolTable;

public class Symbol {
    public enum SymType {
        INT, VOID, ARRAY,CONST
    }

    private String symbolName;
    private SymType symbolType;
    private Integer symbolLevel;

    public Symbol(String symbolName, SymType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
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
