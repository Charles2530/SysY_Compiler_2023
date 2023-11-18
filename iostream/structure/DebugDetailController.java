package iostream.structure;

import backend.generation.mips.Register;
import midend.generation.GenerationMain;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.datastruct.LoopVal;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * DebugDetailController 是一个用于输出调试信息的类
 * 主要用于输出中间代码优化时的各种信息
 * 例如控制流图，支配树，活跃变量分析等
 */
public class DebugDetailController {
    private static BufferedWriter debugDetailOutputStream;
    private static boolean isDebugDetailOutput;

    public static void setBufferedWriter(BufferedWriter debugDetailOutputStream) {
        DebugDetailController.debugDetailOutputStream = debugDetailOutputStream;
    }

    public static void setIsDebugDetailOutput(boolean isDebugDetailOutput) {
        DebugDetailController.isDebugDetailOutput = isDebugDetailOutput;
    }

    /**
     * printDebugDetail 是一个用于输出调试信息的函数
     *
     * @param string 表示调试信息
     */
    public static void printDebugDetail(String string) {
        if (isDebugDetailOutput) {
            try {
                debugDetailOutputStream.write(string + '\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void printDebugDetailWithoutBackSpace(String string) {
        if (isDebugDetailOutput) {
            try {
                debugDetailOutputStream.write(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * printControlFlowGraph 是一个用于输出控制流图的函数
     *
     * @param indBasicBlockFunctionMap 表示入口基本块哈希表
     * @param outBasicBlockFunctionMap 表示出口基本块哈希表
     */
    public static void printControlFlowGraph(HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> indBasicBlockFunctionMap, HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> outBasicBlockFunctionMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Control Flow Graph:");
        for (Function function : indBasicBlockFunctionMap.keySet()) {
            printDebugDetail("  Function: " + function.getName());
            ArrayList<BasicBlock> paramList = new ArrayList<>(
                    indBasicBlockFunctionMap.get(function).keySet());
            paramList.sort(Comparator.comparing(Value::getName));
            for (BasicBlock basicBlock : paramList) {
                printDebugDetail("      BasicBlock: " + basicBlock.getName());
                printDebugDetail("          Ind: ");
                for (BasicBlock ind : indBasicBlockFunctionMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + ind.getName());
                }
                printDebugDetail("          Out: ");
                for (BasicBlock out : outBasicBlockFunctionMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + out.getName());
                }
                printDebugDetail("\n");
            }
        }
        printDebugDetail("\n");
    }

    /**
     * printDominateTree 是一个用于输出支配树的函数
     *
     * @param dominateFunctionHashMap          表示支配哈希表
     * @param dominanceFrontierFunctionHashMap 表示支配前沿哈希表
     * @param parentFunctionHashMap            表示父节点哈希表
     * @param childListFunctionHashMap         表示子节点哈希表
     * @param dominanceTreeDepthHashMap
     */
    public static void printDominateTree(
            HashMap<Function, HashMap<BasicBlock, ArrayList<BasicBlock>>> dominateFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock,
                    ArrayList<BasicBlock>>> dominanceFrontierFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, BasicBlock>> parentFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock,
                    ArrayList<BasicBlock>>> childListFunctionHashMap, HashMap<Function,
            HashMap<BasicBlock, Integer>> dominanceTreeDepthHashMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Dominate Tree:");
        for (Function function : dominateFunctionHashMap.keySet()) {
            printDebugDetail("  Function: " + function.getName());
            ArrayList<BasicBlock> paramList = new ArrayList<>(
                    dominateFunctionHashMap.get(function).keySet());
            paramList.sort(Comparator.comparing(Value::getName));
            for (BasicBlock basicBlock : paramList) {
                printDebugDetail("      BasicBlock: " + basicBlock.getName());
                printDebugDetail("          Dominate: ");
                for (BasicBlock dominate : dominateFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + dominate.getName());
                }
                printDebugDetail("          Dominance Frontier: ");
                for (BasicBlock dominanceFrontier :
                        dominanceFrontierFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + dominanceFrontier.getName());
                }
                printDebugDetail("          Parent: ");
                printDebugDetail("              " + (parentFunctionHashMap.containsKey(function) &&
                        parentFunctionHashMap.get(function).containsKey(basicBlock) &&
                        parentFunctionHashMap.get(function).get(basicBlock) != null ?
                        parentFunctionHashMap.get(function).get(basicBlock).getName() : "null"));
                printDebugDetail("          Child List: ");
                for (BasicBlock child : childListFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + child.getName());
                }
                printDebugDetail("          Depth: ");
                printDebugDetail("              " +
                        dominanceTreeDepthHashMap.get(function).get(basicBlock));
                printDebugDetail("\n");
            }
        }
        printDebugDetail("\n");
    }

    /**
     * printLivenessAnalysis 是一个用于输出活跃变量分析的函数
     *
     * @param inFunctionHashMap  表示入口活跃变量哈希表
     * @param outFunctionHashMap 表示出口活跃变量哈希表
     * @param useFunctionHashMap 表示使用活跃变量哈希表
     * @param defFunctionHashMap 表示定义活跃变量哈希表
     */
    public static void printLivenessAnalysis(
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> inFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> outFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> useFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> defFunctionHashMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Liveness Analysis:");
        for (Function function : inFunctionHashMap.keySet()) {
            printDebugDetail("  Function: " + function.getName());
            ArrayList<BasicBlock> paramList = new ArrayList<>(
                    inFunctionHashMap.get(function).keySet());
            paramList.sort(Comparator.comparing(Value::getName));
            for (BasicBlock basicBlock : paramList) {
                printDebugDetail("      BasicBlock: " + basicBlock.getName());
                printDebugDetail("          In: ");
                for (Value value : inFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + value.toString());
                }
                printDebugDetail("          Out: ");
                for (Value value : outFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + value.toString());
                }
                printDebugDetail("          Use: ");
                for (Value value : useFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + value.toString());
                }
                printDebugDetail("          Def: ");
                for (Value value : defFunctionHashMap.get(function).get(basicBlock)) {
                    printDebugDetail("              " + value.toString());
                }
                printDebugDetail("\n");
            }
        }
        printDebugDetail("\n");
    }

    /**
     * printRegisterValueReflection 是一个用于输出寄存器与值的映射关系的函数
     *
     * @param function        表示函数
     * @param registerHashMap 表示寄存器与值的映射关系
     */
    public static void printRegisterValueReflection(
            Function function, HashMap<Value, Register> registerHashMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Register Value Reflection:");
        printDebugDetail("  Function: " + function.getName());
        printDebugDetail("      Register Value Reflection:");
        ArrayList<Value> paramList = new ArrayList<>(registerHashMap.keySet());
        paramList.sort(Comparator.comparing(Value::getName));
        for (Value value : paramList) {
            printDebugDetail("          " + value.getName() + " ==> " + registerHashMap.get(value));
        }
    }

    /**
     * printInlineFunctionResult 是一个用于输出内联函数的结果的函数
     *
     * @param iterations 表示内联的迭代次数
     */
    public static void printInlineFunctionResult(int iterations) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Inline Function Result:");
        printDebugDetail("  Iterations: " + iterations);
        printDebugDetail(GenerationMain.getModule().toString());
    }

    /**
     * printGlobalVariableNumbering 是一个用于输出全局变量编号的函数
     *
     * @param function                               表示函数
     * @param globalVariableNumberingFunctionHashMap 表示全局变量编号哈希表
     */
    public static void printGlobalVariableNumbering(
            Function function, HashMap<String, Instr> globalVariableNumberingFunctionHashMap) {
        printDebugDetail("  Function: " + function.getName());
        printDebugDetail("      Global Variable Numbering:");
        ArrayList<String> paramList = new ArrayList<>(
                globalVariableNumberingFunctionHashMap.keySet());
        paramList.sort(Comparator.comparing(String::toString));
        for (String string : paramList) {
            printDebugDetail("          " + string + " ==> " +
                    globalVariableNumberingFunctionHashMap.get(string).toString());
        }
        printDebugDetail("\n\n\n");
    }

    /**
     * printLoopAnalysisDetail 用于输出循环分析的详细信息
     *
     * @param loopDepthHashMap  循环深度哈希表
     * @param loopValHashMap    循环集合哈希表
     * @param loopValTopHashMap 顶层循环集合哈希表
     */
    public static void printLoopAnalysisDetail(
            HashMap<Function, HashMap<BasicBlock, Integer>> loopDepthHashMap,
            HashMap<Function, ArrayList<LoopVal>> loopValHashMap,
            HashMap<Function, ArrayList<LoopVal>> loopValTopHashMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Loop Analysis Detail:");
        for (Function function : loopDepthHashMap.keySet()) {
            printDebugDetail("  Function: " + function.getName());
            ArrayList<BasicBlock> paramList = new ArrayList<>(
                    loopDepthHashMap.get(function).keySet());
            paramList.sort(Comparator.comparing(Value::getName));
            for (BasicBlock basicBlock : paramList) {
                printDebugDetail("      BasicBlock: " + basicBlock.getName());
                printDebugDetail("          Loop Depth: " +
                        loopDepthHashMap.get(function).get(basicBlock));
                printDebugDetail("          Loop Val: ");
                for (LoopVal loopVal : loopValHashMap.get(function)) {
                    if (loopVal.getLoopBlocks().contains(basicBlock)) {
                        printDebugDetail("              " + loopVal);
                    }
                }
                printDebugDetail("          Loop Val Top: ");
                for (LoopVal loopVal : loopValTopHashMap.get(function)) {
                    if (loopVal.getLoopBlocks().contains(basicBlock)) {
                        printDebugDetail("              " + loopVal);
                    }
                }
                printDebugDetail("\n");
            }
        }
        printDebugDetail("After Loop Review");
        for (Function function : GenerationMain.getModule().getFunctions()) {
            printDebugDetail("  Function: " + function.getName());
            ArrayList<BasicBlock> sortedList = new ArrayList<>(function.getBasicBlocks());
            sortedList.sort(Comparator.comparing(BasicBlock::getLoopDepth));
            for (BasicBlock block : sortedList) {
                printDebugDetail("      BasicBlock: " + block.getName() +
                        "'s depth is " + block.getLoopDepth());
            }
        }
        printDebugDetail("\n\n\n");
    }

    /**
     * printSideEffectAnalysis 是一个用于输出副作用分析的函数
     *
     * @param module 表示LLVM IR生成的顶级模块
     */
    public static void printSideEffectAnalysis(Module module) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Side Effect Analysis:");
        for (Function function : module.getFunctions()) {
            printDebugDetail("  Function: " + function.getName());
            printDebugDetail("      Side Effect: " + function.getSideEffect());
        }
        printDebugDetail("\n\n\n");
    }

    public static void printGlobalCodeMovementPath(HashMap<Instr, ArrayList<BasicBlock>> pathMap) {
        printDebugDetail(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        printDebugDetail("Global Code Movement Path:");
        for (Instr instr : pathMap.keySet()) {
            if (pathMap.get(instr).size() <= 1) {
                continue;
            }
            printDebugDetail("  Instr: " + instr.toString());
            printDebugDetailWithoutBackSpace("      Path: ");
            BasicBlock pre = null;
            for (BasicBlock basicBlock : pathMap.get(instr)) {
                if (pre != null && pre.equals(basicBlock)) {
                    continue;
                }
                printDebugDetailWithoutBackSpace(basicBlock.getName() + " ==> ");
                pre = basicBlock;
            }
            printDebugDetail(" END");
        }
        printDebugDetail("\n\n\n");
    }
}
