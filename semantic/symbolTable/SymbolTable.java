package semantic.symbolTable;

import semantic.symbolTable.symbol.ConstSymbol;
import semantic.symbolTable.symbol.FuncSymbol;
import semantic.symbolTable.symbol.VarSymbol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private static HashMap<Integer, ArrayList<StackSymbolTable>> symbolTables;
    private static boolean isGlobalArea;
    private static int loopLevel;
    private static FuncSymbol currentFunc;
    private static int curLevel;

    public SymbolTable() {
        SymbolTable.symbolTables = new HashMap<>();
        SymbolTable.isGlobalArea = true;
        SymbolTable.loopLevel = 0;
        SymbolTable.curLevel = -1;
    }

    public static void setGlobalArea(boolean isGlobalArea) {
        SymbolTable.isGlobalArea = isGlobalArea;
    }

    public static void createStackSymbolTable() {
        StackSymbolTable stackSymbolTable = new StackSymbolTable();
        curLevel++;
        if (symbolTables.get(curLevel) == null) {
            symbolTables.put(curLevel, new ArrayList<>());
        }
        symbolTables.get(curLevel).add(stackSymbolTable);
    }

    public static void destroyStackSymbolTable() {
        curLevel--;
    }

    public static void enterLoop() {
        loopLevel++;
    }

    public static void leaveLoop() {
        loopLevel--;
    }

    public static boolean addSymbol(Symbol symbol) {
        StackSymbolTable topTable = symbolTables.get(curLevel)
                .get(symbolTables.get(curLevel).size() - 1);
        if (topTable.getSymbol(symbol.getSymbolName()) != null) {
            return false;
        }
        topTable.addSymbol(symbol);
        return true;
    }

    public static Symbol getSymByName(String name) {
        int level = curLevel;
        while (level >= 0) {
            ArrayList<StackSymbolTable> stackSymbolTables = symbolTables.get(level);
            if (stackSymbolTables == null) {
                level--;
                continue;
            }
            StackSymbolTable topTable = stackSymbolTables.get(stackSymbolTables.size() - 1);
            Symbol symbol = topTable.getSymbol(name);
            if (symbol != null) {
                return symbol;
            }
            level--;
        }
        return null;
    }

    public static int getLoopLevel() {
        return loopLevel;
    }

    public static FuncSymbol getCurrentFunc() {
        return currentFunc;
    }

    public static void setCurrentFunc(FuncSymbol currentFunc) {
        SymbolTable.currentFunc = currentFunc;
    }

    public static void printSymbolTable() {
        System.out.println("Symbol Table:");
        for (int i = 0; i <= symbolTables.size(); i++) {
            System.out.println("Level " + i + ":");
            ArrayList<StackSymbolTable> stackSymbolTables = symbolTables.get(i);
            if (stackSymbolTables == null) {
                continue;
            }
            for (int j = stackSymbolTables.size() - 1; j >= 0; j--) {
                System.out.println("\tStack " + j + ":");
                HashMap<String, Symbol> symbols = stackSymbolTables.get(j).getSymbols();
                for (String key : symbols.keySet()) {
                    Symbol symbol = symbols.get(key);
                    Symbol.SymType type = symbol.getSymbolType();
                    if (symbol instanceof FuncSymbol) {
                        FuncSymbol funcSymbol = (FuncSymbol) symbol;
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tlevel:" + funcSymbol.getSymbolLevel() +
                                " \tparamNum:" + funcSymbol.getParamNum());
                    } else if (symbol instanceof VarSymbol) {
                        VarSymbol varSymbol = (VarSymbol) symbol;
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tdim:" + varSymbol.getDim() +
                                " \tlevel:" + varSymbol.getSymbolLevel() +
                                " \tvalue:" + (varSymbol.getConstValue() == null ?
                                "NAN" : varSymbol.getConstValue()));
                    } else if (symbol instanceof ConstSymbol) {
                        ConstSymbol constSymbol = (ConstSymbol) symbol;
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tdim:" + constSymbol.getDim() +
                                " \tlevel:" + constSymbol.getSymbolLevel() +
                                " \tvalue:" + constSymbol.getConstValue() == null ?
                                "NAN" : constSymbol.getConstValue());
                    }
                }
            }
        }
    }

    public static int getCurlevel() {
        return curLevel;
    }
}
