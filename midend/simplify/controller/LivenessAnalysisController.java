package midend.simplify.controller;

import iostream.structure.DebugDetailController;
import midend.generation.value.Value;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * LiveAnalysisController 是活跃变量分析控制器，
 * 主要用于活跃变量分析
 */
public class LivenessAnalysisController {
    /**
     * module 是LLVM IR生成的顶级模块
     * inFunctionHashMap 存储了每个函数的每个基本块的in集合
     * outFunctionHashMap 存储了每个函数的每个基本块的out集合
     * useFunctionHashMap 存储了每个函数的每个基本块的use集合
     * defFunctionHashMap 存储了每个函数的每个基本块的def集合
     */
    private static Module module;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> inFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> outFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> useFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, HashSet<Value>>> defFunctionHashMap;

    public LivenessAnalysisController(Module module) {
        LivenessAnalysisController.module = module;
        LivenessAnalysisController.inFunctionHashMap = new HashMap<>();
        LivenessAnalysisController.outFunctionHashMap = new HashMap<>();
        LivenessAnalysisController.useFunctionHashMap = new HashMap<>();
        LivenessAnalysisController.defFunctionHashMap = new HashMap<>();
    }

    /**
     * analysis() 是活跃变量分析的主函数
     */
    public void analysis() {
        module.getFunctions().forEach(Function::analysisActiveness);
        DebugDetailController.printLivenessAnalysis(
                inFunctionHashMap, outFunctionHashMap, useFunctionHashMap, defFunctionHashMap);
    }

    /**
     * calculateInOut 方法用于计算每个基本块的in集合和out集合
     * 该函数执行逻辑如下:
     * 1.根据后继的in，求出当前block的out
     * 2.根据公式in = (out - def) + use，求出当前基本块的in
     * 3.如果in集合发生变化，则继续执行while循环，否则结束
     */
    public static void calculateInOut(Function function) {
        ArrayList<BasicBlock> basicBlocks = function.getBasicBlocks();
        boolean change = true;
        while (change) {
            change = false;
            for (int i = basicBlocks.size() - 1; i >= 0; i--) {
                BasicBlock basicBlock = basicBlocks.get(i);
                HashSet<Value> out = new HashSet<>();
                basicBlock.getBlockOutBasicBlock().forEach(
                        successor -> out.addAll(successor.getInBasicBlockHashSet()));
                addOutBlockHashSet(basicBlock, out);
                HashSet<Value> in = new HashSet<>(out);
                in.removeAll(basicBlock.getDefBasicBlockHashSet());
                in.addAll(basicBlock.getUseBasicBlockHashSet());
                HashSet<Value> originIn = basicBlock.getInBasicBlockHashSet();
                addInBlockHashSet(basicBlock, in);
                if (!in.equals(originIn)) {
                    change = true;
                }
            }
        }
    }

    /**
     * addInFunctionHashMap 方法用于向inFunctionHashMap中添加函数和哈希表
     */
    public static void addInFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        inFunctionHashMap.put(function, hashMap);
    }

    /**
     * addOutFunctionHashMap 方法用于向outFunctionHashMap中添加函数和哈希表
     */
    public static void addOutFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        outFunctionHashMap.put(function, hashMap);
    }

    /**
     * addInBlockHashSet 方法用于向inFunctionHashMap中添加基本块和哈希表
     */
    public static void addInBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        inFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        LivenessAnalysisController.getInFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    /**
     * addOutBlockHashSet 方法用于向outFunctionHashMap中添加基本块和哈希表
     */
    public static void addOutBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        outFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        LivenessAnalysisController.getOutFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getInFunctionHashMap(Function function) {
        return inFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getOutFunctionHashMap(Function function) {
        return outFunctionHashMap.get(function);
    }

    public static HashSet<Value> getInBasicBlockHashSet(BasicBlock basicBlock) {
        return LivenessAnalysisController.getInFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    public static HashSet<Value> getOutBasicBlockHashSet(BasicBlock basicBlock) {
        return LivenessAnalysisController.getOutFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    /**
     * addUseFunctionHashMap 方法用于向useFunctionHashMap中添加函数和哈希表
     */
    public static void addUseFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        useFunctionHashMap.put(function, hashMap);
    }

    /**
     * addDefFunctionHashMap 方法用于向defFunctionHashMap中添加函数和哈希表
     */
    public static void addDefFunctionHashMap(
            Function function, HashMap<BasicBlock, HashSet<Value>> hashMap) {
        defFunctionHashMap.put(function, hashMap);
    }

    /**
     * addUseBlockHashSet 方法用于向useFunctionHashMap中添加基本块和哈希表
     */
    public static void addUseBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        useFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        LivenessAnalysisController.getUseFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    /**
     * addDefBlockHashSet 方法用于向defFunctionHashMap中添加基本块和哈希表
     */
    public static void addDefBlockHashSet(BasicBlock basicBlock, HashSet<Value> hashSet) {
        defFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(), k -> new HashMap<>());
        LivenessAnalysisController.getDefFunctionHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, hashSet);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getUseFunctionHashMap(Function function) {
        return useFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock, HashSet<Value>> getDefFunctionHashMap(Function function) {
        return defFunctionHashMap.get(function);
    }

    public static HashSet<Value> getUseBasicBlockHashSet(BasicBlock basicBlock) {
        return LivenessAnalysisController.getUseFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    public static HashSet<Value> getDefBasicBlockHashSet(BasicBlock basicBlock) {
        return LivenessAnalysisController.getDefFunctionHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    /**
     * modifyMerged 方法用于修改合并后的基本块的in集合和out集合
     * 主要用于在后端优化中跳转基本块优化后修改对应的in集合和out集合
     */
    public static void modifyMerged(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        preBasicBlock.getOutBasicBlockHashSet().addAll(basicBlock.getOutBasicBlockHashSet());
        preBasicBlock.getDefBasicBlockHashSet().addAll(basicBlock.getDefBasicBlockHashSet());
        preBasicBlock.getUseBasicBlockHashSet().addAll(basicBlock.getUseBasicBlockHashSet());
    }
}
