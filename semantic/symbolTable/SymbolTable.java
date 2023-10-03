package semantic.symbolTable;

import semantic.symbolTable.symbol.FuncSymbol;

import java.util.HashMap;
import java.util.Stack;

public class SymbolTable {
    private static Stack<StackSymbolTable> symbolTables;
    private static HashMap<String, Stack<StackSymbolTable>> symbolNameTables;
    private static boolean isGlobalArea;
    private static int loopLevel;
    private static FuncSymbol currentFunc;

    public SymbolTable() {
        SymbolTable.symbolTables = new Stack<>();
        SymbolTable.symbolNameTables = new HashMap<>();
        SymbolTable.isGlobalArea = true;
        SymbolTable.loopLevel = 0;
    }

    public static boolean isGlobalArea() {
        return isGlobalArea;
    }

    public static void setGlobalArea(boolean isGlobalArea) {
        SymbolTable.isGlobalArea = isGlobalArea;
    }

    public static void createStackSymbolTable() {
        StackSymbolTable stackSymbolTable = new StackSymbolTable();
        symbolTables.push(stackSymbolTable);
    }

    public static void destroyStackSymbolTable() {
        StackSymbolTable topStack = symbolTables.pop();
        // printSymbolTable();
        topStack.getSymbols().forEach((k, v) -> {
            symbolNameTables.get(k).pop();
        });
    }

    public static void enterLoop() {
        loopLevel++;
    }

    public static void leaveLoop() {
        loopLevel--;
    }

    public static boolean addSymbol(Symbol symbol) {
        StackSymbolTable topTable = symbolTables.peek();
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

    public static Symbol getSymByName(String name) {
        Stack<StackSymbolTable> stackSymbolTables = symbolNameTables.get(name);
        if (stackSymbolTables == null) {
            return null;
        }
        return stackSymbolTables.peek().getSymbol(name);
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
        System.out.println("SymbolTable:");
        symbolTables.forEach((stackSymbolTable) -> {
            stackSymbolTable.getSymbols().forEach((k, v) -> {
                System.out.println(k + ":");
                v.printSymbol();
            });
        });
    }
}
