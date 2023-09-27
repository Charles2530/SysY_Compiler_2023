package semantic.symbolTable;

public class Symbol {
    private String symbolName;
    private String symbolType;

    public Symbol(String symbolName, String symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public String getSymbolType() {
        return symbolType;
    }
}
