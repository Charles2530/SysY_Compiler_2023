package semantic.symbolTable;

import semantic.symbolTable.symbol.FuncSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private static HashMap<Integer, ArrayList<StackSymbolTable>> symbolTables;
    private static boolean isGlobalArea;
    private static int loopLevel;
    private static FuncSymbol currentFunc;
    private static int curlevel;

    public SymbolTable() {
        SymbolTable.symbolTables = new HashMap<>();
        SymbolTable.isGlobalArea = true;
        SymbolTable.loopLevel = 0;
        SymbolTable.curlevel = 0;
        // init global symbol table
        ArrayList<StackSymbolTable> stackSymbolTables = new ArrayList<>();
        stackSymbolTables.add(new StackSymbolTable());
        symbolTables.put(0, stackSymbolTables);
    }

    public static boolean isGlobalArea() {
        return isGlobalArea;
    }

    public static void setGlobalArea(boolean isGlobalArea) {
        SymbolTable.isGlobalArea = isGlobalArea;
    }

    public static void createStackSymbolTable() {
        StackSymbolTable stackSymbolTable = new StackSymbolTable();
        curlevel++;
        if (symbolTables.get(curlevel) == null) {
            symbolTables.put(curlevel, new ArrayList<>());
        }
        symbolTables.get(curlevel).add(stackSymbolTable);
    }

    public static void destroyStackSymbolTable() {
        curlevel--;
    }

    public static void enterLoop() {
        loopLevel++;
    }

    public static void leaveLoop() {
        loopLevel--;
    }

    public static boolean addSymbol(Symbol symbol) {
        StackSymbolTable topTable = symbolTables.get(curlevel).get(symbolTables.get(curlevel).size() - 1);
        if (topTable.getSymbol(symbol.getSymbolName()) != null) {
            return false;
        }
        topTable.addSymbol(symbol);
        return true;
    }

    public static Symbol getSymByName(String name) {
        int level = curlevel;
        while (level >= 0) {
            ArrayList<StackSymbolTable> stackSymbolTables = symbolTables.get(level);
            if (stackSymbolTables == null) {
                level--;
                continue;
            }
            for (int i = stackSymbolTables.size() - 1; i >= 0; i--) {
                Symbol symbol = stackSymbolTables.get(i).getSymbol(name);
                if (symbol != null) {
                    return symbol;
                }
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
        for (int i = 0; i <= curlevel; i++) {
            System.out.println("Level " + i + ":");
            ArrayList<StackSymbolTable> stackSymbolTables = symbolTables.get(i);
            if (stackSymbolTables == null) {
                continue;
            }
            for (int j = stackSymbolTables.size() - 1; j >= 0; j--) {
                System.out.println("Stack " + j + ":");
                HashMap<String, Symbol> symbols = stackSymbolTables.get(j).getSymbols();
                for (String key : symbols.keySet()) {
                    System.out.println(key + " " + symbols.get(key));
                }
            }
        }
    }

    public static FuncSymbol getLatestFunc() {
        return currentFunc;
    }

    public static int getCurlevel() {
        return curlevel;
    }
}
