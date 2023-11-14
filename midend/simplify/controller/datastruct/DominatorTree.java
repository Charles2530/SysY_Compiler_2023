package midend.simplify.controller.datastruct;

import iostream.structure.DebugDetailController;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * DominatorTree 是支配树，
 * 主要用于支配树的构建，
 * 属于一种数据结构
 * 是Mem2Reg的基础
 */
public class DominatorTree {
    /**
     * dominateFunctionHashMap 存储了每个函数的每个基本块的支配集合
     * dominanceFrontierFunctionHashMap 存储了每个函数的每个基本块的支配边界集合
     * parentFunctionHashMap 存储了每个函数的每个基本块的支配父节点
     * childListFunctionHashMap 存储了每个函数的每个基本块的支配子节点集合
     */
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> dominateFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> dominanceFrontierFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock, BasicBlock>> parentFunctionHashMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> childListFunctionHashMap;

    /**
     * build() 是支配树的构建函数，
     * 是构建支配树的主函数
     */
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

    /**
     * buildDominateTree 方法用于构建支配树
     * 支配树换言之就是求解直接支配关系
     * A直接支配B——A严格支配B，且不严格支配任何严格支配B的节点
     */
    private static void buildDominateTree(Function function) {
        function.getBasicBlocks().forEach(basicBlock ->
                basicBlock.getBlockDominateSet().forEach(
                        dominateBlock -> buildDoubleEdge(basicBlock, dominateBlock)));
    }

    /**
     * buildDoubleEdge 方法用于判断并构建双向边
     */
    private static void buildDoubleEdge(BasicBlock basicBlock, BasicBlock dominateBlock) {
        if (isImmediateDominator(basicBlock, dominateBlock)) {
            addDoubleEdge(basicBlock, dominateBlock);
        }
    }

    /**
     * addDoubleEdge 方法用于添加双向边
     */
    private static void addDoubleEdge(BasicBlock fromBlock, BasicBlock toBlock) {
        DominatorTree.addBlockDominateParent(toBlock, fromBlock);
        DominatorTree.addBlockDominateChild(fromBlock, toBlock);
    }

    /**
     * isImmediateDominator 方法用于判断是否是直接支配，
     * 在判断二者为直接支配之前用flag判断二者是否为严格支配
     * 便于决定是否可以添加双向边
     */
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

    /**
     * dfsDominate 方法用于深度优先搜索支配树，
     * 找到所有不被 basicBlock支配的BB，放入reachedSet中，
     * 为后续形成DominateSet打下基础
     */
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

    /**
     * addInBlockHashSet 方法用于向控制流图中添加in集合
     */
    public static void addFunctionDominate(Function function,
                                           HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.dominateFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominate(Function function) {
        return DominatorTree.dominateFunctionHashMap.get(function);
    }

    /**
     * addFunctionDominate 方法用于向控制流图中添加dominate集合
     */
    public static void addFunctionDominanceFrontier(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.dominanceFrontierFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominanceFrontier(Function function) {
        return DominatorTree.dominanceFrontierFunctionHashMap.get(function);
    }

    /**
     * addFunctionDominateParent 方法用于向控制流图中添加dominate父节点
     */
    public static void addFunctionDominateParent(Function function,
                                                 HashMap<BasicBlock, BasicBlock> hashMap) {
        DominatorTree.parentFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock, BasicBlock> getFunctionDominateParent(Function function) {
        return DominatorTree.parentFunctionHashMap.get(function);
    }

    /**
     * addFunctionDominateChildList 方法用于向控制流图中添加dominate子节点集合
     */
    public static void addFunctionDominateChildList(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> hashMap) {
        DominatorTree.childListFunctionHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionDominateChildList(Function function) {
        return DominatorTree.childListFunctionHashMap.get(function);
    }

    /**
     * addBlockDominateSet 方法用于向控制流图中添加基本块的支配集合
     */
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

    /**
     * addBlockDominanceFrontier 方法用于向控制流图中添加基本块的支配边界集合
     */
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

    /**
     * addBlockDominateParent 方法用于向控制流图中添加基本块的支配父节点
     */
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

    /**
     * addBlockDominateChildList 方法用于向控制流图中添加基本块的支配子节点集合
     */
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

    /**
     * addBlockDominateFrontierEdge 方法用于向控制流图中添加基本块的支配边界集合
     */
    public static void addBlockDominanceFrontierEdge(BasicBlock runner, BasicBlock to) {
        dominanceFrontierFunctionHashMap.computeIfAbsent(runner.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominanceFrontier(runner.getBelongingFunc())
                .get(runner).add(to);
    }

    /**
     * addBlockDominateChild 方法用于向控制流图中添加基本块的支配子节点
     */
    public static void addBlockDominateChild(BasicBlock runner, BasicBlock to) {
        childListFunctionHashMap.computeIfAbsent(runner.getBelongingFunc(),
                k -> new HashMap<>());
        DominatorTree.getFunctionDominateChildList(runner.getBelongingFunc())
                .get(runner).add(to);
    }

    /**
     * modifyMerged 方法用于修改合并后的支配树，
     * 主要用于在后端优化中跳转基本块优化后修改对应的支配树
     */
    public static void modifyMerged(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        DominatorTree.modifyMergedDominateSet(preBasicBlock, basicBlock);
        DominatorTree.modifyMergedDominanceFrontier(preBasicBlock, basicBlock);
        DominatorTree.modifyMergedDominateParent(preBasicBlock, basicBlock);
        DominatorTree.modifyMergedDominateChildList(preBasicBlock, basicBlock);
    }

    /**
     * modifyMerged 方法用于修改合并后的支配树，
     * 主要用于在后端优化中跳转基本块优化后修改对应的支配树
     */
    private static void modifyMergedDominateSet(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        ArrayList<BasicBlock> preDominateSet = preBasicBlock.getBlockDominateSet();
        ArrayList<BasicBlock> basicDominateSet = basicBlock.getBlockDominateSet();
        for (BasicBlock block : basicDominateSet) {
            if (!preDominateSet.contains(block)) {
                preDominateSet.add(block);
            }
        }
        DominatorTree.addBlockDominateSet(preBasicBlock, preDominateSet);
    }

    /**
     * modifyMerged 方法用于修改合并后的支配树，
     * 主要用于在后端优化中跳转基本块优化后修改对应的支配树边缘
     */
    private static void modifyMergedDominanceFrontier(
            BasicBlock preBasicBlock, BasicBlock basicBlock) {
        ArrayList<BasicBlock> preDominanceFrontier = preBasicBlock.getBlockDominanceFrontier();
        ArrayList<BasicBlock> basicDominanceFrontier = basicBlock.getBlockDominanceFrontier();
        for (BasicBlock block : basicDominanceFrontier) {
            if (!preDominanceFrontier.contains(block)) {
                preDominanceFrontier.add(block);
            }
        }
        DominatorTree.addBlockDominanceFrontier(preBasicBlock, preDominanceFrontier);
    }

    /**
     * modifyMerged 方法用于修改合并后的支配树，
     * 主要用于在后端优化中跳转基本块优化后修改对应的支配树父类
     */
    private static void modifyMergedDominateParent(
            BasicBlock preBasicBlock, BasicBlock basicBlock) {
        BasicBlock preDominateParent = preBasicBlock.getBlockDominateParent();
        BasicBlock basicDominateParent = basicBlock.getBlockDominateParent();
        if (preDominateParent != null && basicDominateParent != null) {
            if (!preDominateParent.equals(basicDominateParent)) {
                DominatorTree.addBlockDominateParent(preBasicBlock, basicDominateParent);
            }
        } else if (preDominateParent == null && basicDominateParent != null) {
            DominatorTree.addBlockDominateParent(preBasicBlock, basicDominateParent);
        }
    }

    /**
     * modifyMerged 方法用于修改合并后的支配树，
     * 主要用于在后端优化中跳转基本块优化后修改对应的支配树子类
     */
    private static void modifyMergedDominateChildList(
            BasicBlock preBasicBlock, BasicBlock basicBlock) {
        ArrayList<BasicBlock> preDominateChildList = preBasicBlock.getBlockDominateChildList();
        ArrayList<BasicBlock> basicDominateChildList = basicBlock.getBlockDominateChildList();
        for (BasicBlock block : basicDominateChildList) {
            if (!preDominateChildList.contains(block)) {
                preDominateChildList.add(block);
            }
        }
        DominatorTree.addBlockDominateChildList(preBasicBlock, preDominateChildList);
    }

}
