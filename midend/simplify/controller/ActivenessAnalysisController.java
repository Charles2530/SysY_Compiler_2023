package midend.simplify.controller;

import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.simplify.controller.datastruct.ControlFlowGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ActivenessAnalysisController {
    private static Module module;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> inFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> outFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> useFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> defFunctionHashMap;

    public ActivenessAnalysisController(Module module) {
        ActivenessAnalysisController.module = module;
        ActivenessAnalysisController.inFunctionHashMap = new HashMap<>();
        ActivenessAnalysisController.outFunctionHashMap = new HashMap<>();
        ActivenessAnalysisController.useFunctionHashMap = new HashMap<>();
        ActivenessAnalysisController.defFunctionHashMap = new HashMap<>();
    }

    public static void calculateInOut(Function function) {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
        boolean change = true;
        while (change) {
            change = false;
            for (int i = basicBlocks.size() - 1; i >= 0; i--) {
                BasicBlock basicBlock = basicBlocks.get(i);
                HashSet<Value> out = new HashSet<>();
                addOutBlockHashSet(basicBlock, out);
                for (BasicBlock successor : ControlFlowGraph.getBlockOutBasicBlock(basicBlock)) {
                    out.addAll(getInBasicBlockHashSet(successor));
                    getOutFunctionHashMap(function).put(basicBlock, out);
                }
                HashSet<Value> originIn = getInBasicBlockHashSet(basicBlock);
                HashSet<Value> in = new HashSet<>(out);
                in.removeAll(getDefBasicBlockHashSet(basicBlock));
                in.addAll(getUseBasicBlockHashSet(basicBlock));
                if (!in.equals(originIn)) {
                    change = true;
                    getInFunctionHashMap(function).put(basicBlock, in);
                }
            }
        }
    }

    public void analysis() {
        module.getFunctions().forEach(Function::analysisActiveness);
    }

    public static void addInFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        inFunctionHashMap.put(function, hashMap);
    }

    public static void addOutFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        outFunctionHashMap.put(function, hashMap);
    }

    public static void addInBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        inFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        ActivenessAnalysisController.getInFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static void addOutBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        outFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        ActivenessAnalysisController.getOutFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getInFunctionHashMap(Function function) {
        return inFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getOutFunctionHashMap(Function function) {
        return outFunctionHashMap.get(function);
    }

    public static HashSet<Value> getInBasicBlockHashSet(BasicBlock basicBlock) {
        return ActivenessAnalysisController.getInFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    public static HashSet<Value> getOutBasicBlockHashSet(BasicBlock basicBlock) {
        return ActivenessAnalysisController.getOutFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    public static void addUseFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        useFunctionHashMap.put(function, hashMap);
    }

    public static void addDefFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        defFunctionHashMap.put(function, hashMap);
    }

    public static void addUseBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        useFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        ActivenessAnalysisController.getUseFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static void addDefBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        defFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        ActivenessAnalysisController.getDefFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getUseFunctionHashMap(Function function) {
        return useFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getDefFunctionHashMap(Function function) {
        return defFunctionHashMap.get(function);
    }

    public static HashSet<Value> getUseBasicBlockHashSet(BasicBlock basicBlock) {
        return ActivenessAnalysisController.getUseFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    public static HashSet<Value> getDefBasicBlockHashSet(BasicBlock basicBlock) {
        return ActivenessAnalysisController.getDefFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }
}
