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
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominateHashMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> dominanceFrontierFunctionHashMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominanceFrontierHashMap;
    private static HashMap<Function, HashMap<BasicBlock, BasicBlock>> parentFunctionHashMap;
    private static HashMap<BasicBlock, BasicBlock> parent;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> childListFunctionHashMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> childList;

    public static void build(Module module) {
        DominatorTree.dominateFunctionHashMap = new HashMap<>();
        DominatorTree.dominanceFrontierFunctionHashMap = new HashMap<>();
        DominatorTree.parentFunctionHashMap = new HashMap<>();
        DominatorTree.childListFunctionHashMap = new HashMap<>();
        for (Function function : module.getFunctions()) {
            DominatorTree.dominateHashMap = new HashMap<>();
            DominatorTree.dominateFunctionHashMap.put(function, DominatorTree.dominateHashMap);
            DominatorTree.dominanceFrontierHashMap = new HashMap<>();
            DominatorTree.dominanceFrontierFunctionHashMap.put(function,
                    DominatorTree.dominanceFrontierHashMap);
            DominatorTree.parent = new HashMap<>();
            DominatorTree.parentFunctionHashMap.put(function, DominatorTree.parent);
            DominatorTree.childList = new HashMap<>();
            DominatorTree.childListFunctionHashMap.put(function, DominatorTree.childList);
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                DominatorTree.dominateHashMap.put(basicBlock, new ArrayList<>());
                DominatorTree.dominanceFrontierHashMap.put(basicBlock, new ArrayList<>());
                DominatorTree.parent.put(basicBlock, null);
                DominatorTree.childList.put(basicBlock, new ArrayList<>());
            }
            function.searchBlockDominateSet();
            DominatorTree.buildDominateTree(function);
            function.searchBlockDominanceFrontier();
        }
        DebugDetailController.printDominateTree(dominateFunctionHashMap,
                dominanceFrontierFunctionHashMap, parentFunctionHashMap, childListFunctionHashMap);
    }

    private static void buildDominateTree(Function function) {
        function.getBasicBlocks().forEach(basicBlock -> dominateHashMap.get(basicBlock)
                .forEach(dominateBlock -> buildDoubleEdge(basicBlock, dominateBlock)));
    }

    private static void buildDoubleEdge(BasicBlock basicBlock, BasicBlock dominateBlock) {
        if (isImmediateDominator(basicBlock, dominateBlock)) {
            addDoubleEdge(basicBlock, dominateBlock);
        }
    }

    private static void addDoubleEdge(BasicBlock fromBlock, BasicBlock toBlock) {
        DominatorTree.parent.put(toBlock, fromBlock);
        DominatorTree.childList.get(fromBlock).add(toBlock);
    }

    private static boolean isImmediateDominator(BasicBlock basicBlock, BasicBlock dominateBlock) {
        boolean flag = DominatorTree.getBlockDominateSet(basicBlock).contains(dominateBlock) &&
                !dominateBlock.equals(basicBlock);
        for (BasicBlock midBlock : DominatorTree.getBlockDominateSet(basicBlock)) {
            if (!midBlock.equals(dominateBlock) && !midBlock.equals(basicBlock)
                    && DominatorTree.getBlockDominateSet(midBlock).contains(dominateBlock)) {
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
        for (BasicBlock child : ControlFlowGraph.getBlockOutBasicBlock(entry)) {
            if (!reachedSet.contains(child)) {
                DominatorTree.dfsDominate(child, basicBlock, reachedSet);
            }
        }
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominate(Function function) {
        return DominatorTree.dominateFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominanceFrontier(Function function) {
        return DominatorTree.dominanceFrontierFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock, BasicBlock> getFunctionDominateParent(Function function) {
        return DominatorTree.parentFunctionHashMap.get(function);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominateChildList(Function function) {
        return DominatorTree.childListFunctionHashMap.get(function);
    }

    public static void addBlockDominateSet(BasicBlock basicBlock, ArrayList<BasicBlock> domList) {
        DominatorTree.getFunctionDominate(basicBlock.getBelongingFunc()).put(basicBlock,
                domList);
    }

    public static ArrayList<BasicBlock> getBlockDominateSet(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominate(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static void addBlockDominanceFrontier(BasicBlock runner, BasicBlock to) {
        DominatorTree.getFunctionDominanceFrontier(runner.getBelongingFunc())
                .get(runner).add(to);
    }

    public static ArrayList<BasicBlock> getBlockDominanceFrontier(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominanceFrontier(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static BasicBlock getBlockDominateParent(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominateParent(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static ArrayList<BasicBlock> getBlockDominateChildList(BasicBlock basicBlock) {
        return DominatorTree.getFunctionDominateChildList(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }
}
