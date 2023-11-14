package frontend.generation.semantic.symtable;

/**
 * Symbol 表示符号表中的表项
 */
public class Symbol {
    /**
     * SymType 是符号表项的类型,分为INT,VOID,CONST三种
     * symbolName 是符号表项的名字
     * symbolType 是符号表项的类型
     * symbolLevel 是符号表项所处的层次
     */
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
