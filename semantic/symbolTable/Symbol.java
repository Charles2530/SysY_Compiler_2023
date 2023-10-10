package semantic.symbolTable;

public class Symbol {

    public enum SymType {
        INT, VOID, CONST
    }

    private String symbolName;
    private SymType symbolType;
    private Integer symbolLevel;

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

    public void printSymbol() {
        System.out.println("Symbol{" +
                "symbolName='" + symbolName + '\'' +
                ", symbolType=" + symbolType +
                ", symbolLevel=" + symbolLevel +
                '}');
    }

}
