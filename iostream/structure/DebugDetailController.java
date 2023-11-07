package iostream.structure;

import backend.generation.mips.Register;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.user.Function;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class DebugDetailController {
    private static BufferedWriter debugDetailOutputStream;
    private static boolean isDebugDetailOutput;

    public static void setBufferedWriter(BufferedWriter debugDetailOutputStream) {
        DebugDetailController.debugDetailOutputStream = debugDetailOutputStream;
    }

    public static void setIsDebugDetailOutput(boolean isDebugDetailOutput) {
        DebugDetailController.isDebugDetailOutput = isDebugDetailOutput;
    }

    private static void printDebugDetail(String string) {
        if (isDebugDetailOutput) {
            try {
                debugDetailOutputStream.write(string + '\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void printControlFlowGraph(HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> indBasicBlockFunctionMap, HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> outBasicBlockFunctionMap) {
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

    public static void printDominateTree(
            HashMap<Function, HashMap<BasicBlock, ArrayList<BasicBlock>>> dominateFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock,
                    ArrayList<BasicBlock>>> dominanceFrontierFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, BasicBlock>> parentFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock,
                    ArrayList<BasicBlock>>> childListFunctionHashMap) {
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
                printDebugDetail("\n");
            }
        }
        printDebugDetail("\n");
    }

    public static void printLivenessAnalysis(
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> inFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> outFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> useFunctionHashMap,
            HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> defFunctionHashMap) {
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

    public static void printRegisterValueReflection(
            Function function, HashMap<Value, Register> registerHashMap) {
        printDebugDetail("Function: " + function.getName());
        printDebugDetail("  Register Value Reflection:");
        ArrayList<Value> paramList = new ArrayList<>(registerHashMap.keySet());
        paramList.sort(Comparator.comparing(Value::getName));
        for (Value value : paramList) {
            printDebugDetail("      " + value.getName() + " ==> " + registerHashMap.get(value));
        }
    }
}
