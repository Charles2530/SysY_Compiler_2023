package midend.simplify.controller.datastruct;

import iostream.structure.DebugDetailController;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DominatorTree {
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> dominateFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> dominanceFrontierFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, BasicBlock>> parentFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> childListFunctionHashMap;

    public static void build(Module module) {
        DominatorTree.dominateFunctionHashMap = new HashMap<>();
        DominatorTree.dominanceFrontierFunctionHashMap = new HashMap<>();
        DominatorTree.parentFunctionHashMap = new HashMap<>();
        DominatorTree.childListFunctionHashMap = new HashMap<>();
        for (Function function : module.getFunctions()) {
            DominatorTree.addFunctionDominate(function, new HashMap<>());
            DominatorTree.addFunctionDominanceFrontier(function, new HashMap<>());
            DominatorTree.addFunctionDominateParent(function, new HashMap<>());
            DominatorTree.addFunctionDominateChildList(function, new HashMap<>());
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                DominatorTree.addBlockDominateSet(basicBlock, new ArrayList<>());
                DominatorTree.addBlockDominanceFrontier(basicBlock, new ArrayList<>());
                DominatorTree.addBlockDominateParent(basicBlock, null);
                DominatorTree.addBlockDominateChildList(basicBlock, new ArrayList<>());
            }
            function.searchBlockDominateSet();
            DominatorTree.buildDominateTree(function);
            function.searchBlockDominanceFrontier();
        }
        DebugDetailController.printDominateTree(dominateFunctionHashMap,
                dominanceFrontierFunctionHashMap, parentFunctionHashMap, childListFunctionHashMap);
    }

    private static void buildDominateTree(Function function) {
        function.getBasicBlocks().forEach(basicBlock ->
                basicBlock.getBlockDominateSet().forEach(
                        dominateBlock -> buildDoubleEdge(basicBlock, dominateBlock)));
    }

    private static void buildDoubleEdge(BasicBlock basicBlock, BasicBlock dominateBlock) {
        if (isImmediateDominator(basicBlock, dominateBlock)) {
            addDoubleEdge(basicBlock, dominateBlock);
        }
    }

    private static void addDoubleEdge(BasicBlock fromBlock, BasicBlock toBlock) {
        DominatorTree.addBlockDominateParent(toBlock, fromBlock);
        DominatorTree.addBlockDominateChild(fromBlock, toBlock);
    }

    private static boolean isImmediateDominator(BasicBlock basicBlock, BasicBlock dominateBlock) {
        boolean flag = basicBlock.getBlockDominateSet().contains(dominateBlock) &&
                !dominateBlock.equals(basicBlock);
        for (BasicBlock midBlock : basicBlock.getBlockDominateSet()) {
            if (!midBlock.equals(dominateBlock) && !midBlock.equals(basicBlock)
                    && midBlock.getBlockDominateSet().contains(dominateBlock)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static void dfsDominate(BasicBlock entry, BasicBlock basicBlock,
                                   HashSet<BasicBlock> reachedSet) {
        if (entry.equals(basicBlock)) {
            return;
        }
        reachedSet.add(entry);
        for (BasicBlock child : entry.getBlockOutBasicBlock()) {
            if (!reachedSet.contains(child)) {
                DominatorTree.dfsDominate(child, basicBlock, reachedSet);
            }
        }
    }

    public static void addFunctionDominate(Function function,
                                           HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.dominateFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominate(Function function) {
        return DominatorTree.dominateFunctionHashMap.get(function);
    }

    public static void addFunctionDominanceFrontier(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.dominanceFrontierFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominanceFrontier(Function function) {
        return DominatorTree.dominanceFrontierFunctionHashMap.get(function);
    }

    public static void addFunctionDominateParent(Function function,
                                                 HashMap<BasicBlock, BasicBlock> hashMap) {
        DominatorTree.parentFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock, BasicBlock> getFunctionDominateParent(Function function) {
        return DominatorTree.parentFunctionHashMap.get(function);
    }

    public static void addFunctionDominateChildList(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.childListFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominateChildList(Function function) {
        return DominatorTree.childListFunctionHashMap.get(function);
    }

    public static void addBlockDominateSet(BasicBlock basicBlock, ArrayList<BasicBlock> domList) {
        dominateFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominate(basicBlock.getBelongingFunc()).put(basicBlock,
                domList);
    }

    public static ArrayList<BasicBlock> getBlockDominateSet(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominate(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static void addBlockDominanceFrontier(
            BasicBlock basicBlock, ArrayList<BasicBlock> domList) {
        dominanceFrontierFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominanceFrontier(basicBlock.getBelongingFunc())
                .put(basicBlock, domList);
    }

    public static ArrayList<BasicBlock> getBlockDominanceFrontier(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominanceFrontier(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static void addBlockDominateParent(BasicBlock basicBlock, BasicBlock domParent) {
        parentFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominateParent(basicBlock.getBelongingFunc())
                .put(basicBlock, domParent);
    }

    public static BasicBlock getBlockDominateParent(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominateParent(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static void addBlockDominateChildList(
            BasicBlock basicBlock, ArrayList<BasicBlock> domList) {
        childListFunctionHashMap.computeIfAbsent(basicBlock.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominateChildList(basicBlock.getBelongingFunc())
                .put(basicBlock, domList);
    }

    public static ArrayList<BasicBlock> getBlockDominateChildList(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominateChildList(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static void addBlockDominanceFrontierEdge(BasicBlock runner, BasicBlock to) {
        dominanceFrontierFunctionHashMap.computeIfAbsent(runner.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominanceFrontier(runner.getBelongingFunc())
                .get(runner).add(to);
    }

    public static void addBlockDominateChild(BasicBlock runner, BasicBlock to) {
        childListFunctionHashMap.computeIfAbsent(runner.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominateChildList(runner.getBelongingFunc())
                .get(runner).add(to);
    }
}
