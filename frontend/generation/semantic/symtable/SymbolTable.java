package frontend.generation.semantic.symtable;

import frontend.generation.semantic.symtable.symbol.varsymbol.ConstSymbol;
import frontend.generation.semantic.symtable.symbol.FuncSymbol;
import frontend.generation.semantic.symtable.symbol.varsymbol.IntSymbol;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SymbolTable 是符号表数据结构，使用的是树状符号表
 */
public class SymbolTable {
    /**
     * symbolTable 是符号表的树状结构，使用HashMap实现,每个
     * 符号表项是一个StackSymbolTable数组,每个StackSymbolTable
     * 是一个栈结构，每个栈结构中存储的是同一层次的符号表项(类似栈式符号表)
     * isGlobalArea 是用于标记当前符号表是否处于全局区
     * loopLevel 是用于标记当前符号表处于第几层循环中
     * currentFunc 是用于标记当前符号表所处的函数
     * curLevel 是用于标记当前符号表所处的层次
     */
    private static HashMap<Integer, ArrayList<StackSymbolTable>> symbolTables;
    private static boolean isGlobalArea;
    private static int loopLevel;
    private static FuncSymbol currentFunc;
    private static int curLevel;

    /**
     * 初始化符号表
     */
    public static void init() {
        SymbolTable.symbolTables = new HashMap<>();
        SymbolTable.isGlobalArea = true;
        SymbolTable.loopLevel = 0;
        SymbolTable.curLevel = -1;
    }

    /**
     * 清空符号表，主要用于重建符号表前
     */
    public static void clear() {
        SymbolTable.symbolTables.clear();
        SymbolTable.isGlobalArea = true;
        SymbolTable.loopLevel = 0;
        SymbolTable.curLevel = -1;
    }

    /**
     * setGlobalArea 是用于设置当前符号表是否处于全局区的函数
     */
    public static void setGlobalArea(boolean isGlobalArea) {
        SymbolTable.isGlobalArea = isGlobalArea;
    }

    public static boolean isIsGlobalArea() {
        return isGlobalArea;
    }

    /**
     * createStackSymbolTable() 用于在当前所处层次中新建一个栈式符号表
     */
    public static void createStackSymbolTable() {
        StackSymbolTable stackSymbolTable = new StackSymbolTable();
        curLevel++;
        symbolTables.computeIfAbsent(curLevel, k -> new ArrayList<>());
        symbolTables.get(curLevel).add(stackSymbolTable);
    }

    /**
     * destroyStackSymbolTable() 用于销毁当前所处层次中的栈式符号表
     */
    public static void destroyStackSymbolTable() {
        curLevel--;
    }

    /**
     * enterLoop() 用于进入循环
     */
    public static void enterLoop() {
        loopLevel++;
        createStackSymbolTable();
    }

    public static void leaveLoop() {
        loopLevel--;
        destroyStackSymbolTable();
    }

    /**
     * addSymbol() 用于向当前所处层次的栈式符号表中添加符号
     *
     * @return 添加成功返回true，否则返回false,表示符号已存在
     */
    public static boolean addSymbol(Symbol symbol) {
        StackSymbolTable topTable = symbolTables.get(curLevel)
                .get(symbolTables.get(curLevel).size() - 1);
        if (topTable.getSymbol(symbol.getSymbolName()) != null) {
            return false;
        }
        topTable.addSymbol(symbol);
        return true;
    }

    /**
     * getSymByName() 用于根据符号名获取符号
     */
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

    /**
     * setCurrentFunc() 用于设置当前符号表所处的函数
     */
    public static void setCurrentFunc(FuncSymbol currentFunc) {
        SymbolTable.currentFunc = currentFunc;
    }

    public static int getCurLevel() {
        return curLevel;
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
                    if (symbol instanceof FuncSymbol funcSymbol) {
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tlevel:" + funcSymbol.getSymbolLevel() +
                                " \tparamNum:" + funcSymbol.getParamNum());
                    } else if (symbol instanceof IntSymbol intSymbol) {
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tdim:" + intSymbol.getDim() +
                                " \tlevel:" + intSymbol.getSymbolLevel() +
                                " \tvalue:" + (intSymbol.getConstValue() == null ?
                                "NAN" : intSymbol.getConstValue()));
                    } else if (symbol instanceof ConstSymbol constSymbol) {
                        System.out.println("\t\t" + type + " \t" + key +
                                " \tdim:" + constSymbol.getDim() +
                                " \tlevel:" + constSymbol.getSymbolLevel() +
                                " \tvalue:" + (constSymbol.getConstValue() == null ?
                                "NAN" : constSymbol.getConstValue()));
                    }
                }
            }
        }
    }

}
