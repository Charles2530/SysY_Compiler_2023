package midend.simplify.controller.datastruct;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DominatorTree {
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominateHashMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> dominanceFrontierHashMap;
    private static HashMap<BasicBlock, BasicBlock> parent;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> childList;

    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            DominatorTree.dominateHashMap = new HashMap<>();
            DominatorTree.dominanceFrontierHashMap = new HashMap<>();
            DominatorTree.parent = new HashMap<>();
            DominatorTree.childList = new HashMap<>();
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
    }

    private static void buildDominateTree(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            for (BasicBlock dominateBlock : DominatorTree.dominateHashMap.get(basicBlock)) {
                if (isImmediateDominator(basicBlock, dominateBlock)) {
                    DominatorTree.parent.put(basicBlock, dominateBlock);
                    DominatorTree.childList.get(basicBlock).add(dominateBlock);
                }
            }
        }
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            basicBlock.updateDominateTree(parent.get(basicBlock), childList.get(basicBlock));
        }
        function.updateDominateTree(parent, childList);
    }

    private static boolean isImmediateDominator(BasicBlock basicBlock, BasicBlock dominateBlock) {
        boolean flag = basicBlock.getDominateSet().contains(dominateBlock) &&
                !dominateBlock.equals(basicBlock);
        for (BasicBlock midBlock : basicBlock.getDominateSet()) {
            if (!midBlock.equals(dominateBlock) && !midBlock.equals(basicBlock)
                    && basicBlock.getDominateSet().contains(midBlock)) {
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
        reachedSet.add(basicBlock);
        for (BasicBlock child : DominatorTree.childList.get(basicBlock)) {
            if (!reachedSet.contains(child)) {
                DominatorTree.dfsDominate(entry, child, reachedSet);
            }
        }
    }

    public static void addDominateSet(BasicBlock basicBlock, ArrayList<BasicBlock> domList) {
        DominatorTree.dominateHashMap.put(basicBlock, domList);
    }

    public static void addBlockDominanceFrontier(BasicBlock runner, BasicBlock to) {
        DominatorTree.dominanceFrontierHashMap.get(runner).add(to);
    }

    public static ArrayList<BasicBlock> getBlockDominanceFrontier(BasicBlock basicBlock) {
        return DominatorTree.dominanceFrontierHashMap.get(basicBlock);
    }
}
