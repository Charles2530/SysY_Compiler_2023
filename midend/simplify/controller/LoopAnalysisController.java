package midend.simplify.controller;

import iostream.structure.DebugDetailController;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.simplify.controller.datastruct.LoopVal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * LoopAnalysisController 是循环分析控制器，
 * 主要用于循环分析
 */
public class LoopAnalysisController {
    /**
     * loopDepthHashMap 记录了每个函数所包含的每个块的循环深度
     * loopValHashMap 记录了每个函数所包含的循环集合
     * loopValTopHashMap 记录了每个函数所包含的顶层循环集合
     */
    private static HashMap<Function, HashMap<BasicBlock, Integer>> loopDepthHashMap;
    private static HashMap<Function, ArrayList<LoopVal>> loopValHashMap;
    private static HashMap<Function, ArrayList<LoopVal>> loopValTopHashMap;

    /**
     * analysis 方法用于分析循环,是循环分析的主函数
     * 遍历一遍Function，找出所有循环
     *
     * @param module 是LLVM IR生成的顶级模块
     */
    public static void analysis(Module module) {
        LoopAnalysisController.init();
        module.getFunctions().forEach(Function::loopAnalysis);
        DebugDetailController.printLoopAnalysisDetail(
                loopDepthHashMap, loopValHashMap, loopValTopHashMap);
    }

    /**
     * init 方法用于初始化该 LoopAnalysisController
     */
    private static void init() {
        LoopAnalysisController.loopDepthHashMap = new HashMap<>();
        LoopAnalysisController.loopValHashMap = new HashMap<>();
        LoopAnalysisController.loopValTopHashMap = new HashMap<>();
    }

    /**
     * addFunctionLoopDepthHashMap 方法用于添加循环深度哈希表
     */
    public static void addFunctionLoopDepthHashMap(
            Function function, HashMap<BasicBlock, Integer> hashMap) {
        LoopAnalysisController.loopDepthHashMap.put(function, hashMap);
    }

    public static HashMap<BasicBlock, Integer> getFunctionLoopDepthHashMap(Function function) {
        return LoopAnalysisController.loopDepthHashMap.get(function);
    }

    /**
     * addBlockLoopDepth 方法用于添加循环深度哈希表
     */
    public static void addBlockLoopDepth(BasicBlock basicBlock, Integer integer) {
        LoopAnalysisController.getFunctionLoopDepthHashMap(
                basicBlock.getBelongingFunc()).put(basicBlock, integer);
    }

    public static Integer getBlockLoopDepth(BasicBlock basicBlock) {
        return LoopAnalysisController.getFunctionLoopDepthHashMap(
                basicBlock.getBelongingFunc()).get(basicBlock);
    }

    /**
     * addFunctionLoopVal 方法用于添加循环哈希表
     */
    public static void addFunctionLoopVal(
            Function function, ArrayList<LoopVal> arrays) {
        LoopAnalysisController.loopValHashMap.put(function, arrays);
    }

    public static ArrayList<LoopVal> getFunctionLoopVal(Function function) {
        return LoopAnalysisController.loopValHashMap.get(function);
    }

    public static void addLoopVal(LoopVal loop) {
        LoopAnalysisController.getFunctionLoopVal(
                loop.getEntryBlock().getBelongingFunc()).add(loop);
    }

    /**
     * addFunctionLoopValTop 方法用于添加循环哈希表
     */
    public static void addFunctionLoopValTop(
            Function function, ArrayList<LoopVal> arrays) {
        LoopAnalysisController.loopValTopHashMap.put(function, arrays);
    }

    public static ArrayList<LoopVal> getFunctionLoopValTop(Function function) {
        return LoopAnalysisController.loopValTopHashMap.get(function);
    }

    public static void addLoopValTop(LoopVal loop) {
        LoopAnalysisController.getFunctionLoopValTop(
                loop.getEntryBlock().getBelongingFunc()).add(loop);
    }

    /**
     * addLoopIntoGraph 方法用于将循环体的块加入循环中
     * 采用的是反转 CFG 图的方式
     *
     * @param latchBlocks 栓块集合
     * @param loop        当前循环
     */
    public static void addLoopIntoGraph(ArrayList<BasicBlock> latchBlocks, LoopVal loop) {
        ArrayList<BasicBlock> blocks = new ArrayList<>(latchBlocks);
        while (!blocks.isEmpty()) {
            BasicBlock block = blocks.remove(0);
            LoopVal subloop = block.getLoopVal();
            if (subloop == null) {
                block.setLoopVal(loop);
                if (block.equals(loop.getEntryBlock())) {
                    continue;
                }
                blocks.addAll(block.getBlockIndBasicBlock());
            } else {
                LoopVal parent = subloop.getParentLoop();
                while (parent != null) {
                    subloop = parent;
                    parent = parent.getParentLoop();
                }
                if (subloop.equals(loop)) {
                    continue;
                }
                subloop.setParentLoop(loop);
                for (BasicBlock indBasicBlock :
                        subloop.getEntryBlock().getBlockIndBasicBlock()) {
                    if (indBasicBlock.getLoopVal() != subloop) {
                        blocks.add(indBasicBlock);
                    }
                }
            }
        }
    }

    /**
     * addLoopSons 方法用于建立外循环对内循环的关系
     * 该方法会将外循环的所有子循环添加到外循环的子循环集合中
     * 登记所有的循环,登记循环深度
     *
     * @param root 是循环的根节点
     */
    public static void addLoopSons(BasicBlock root) {
        Stack<BasicBlock> stack = new Stack<>();
        HashSet<BasicBlock> visited = new HashSet<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            BasicBlock block = stack.pop();
            visited.add(block);
            LoopVal subloop = block.getLoopVal();
            if (subloop != null && block.equals(subloop.getEntryBlock())) {
                LoopVal parent = subloop.getParentLoop();
                if (parent != null) {
                    parent.addSubLoop(subloop);
                    LoopAnalysisController.getFunctionLoopVal(root.getBelongingFunc()).add(subloop);
                } else {
                    LoopAnalysisController.getFunctionLoopValTop(
                            root.getBelongingFunc()).add(subloop);
                    LoopAnalysisController.getFunctionLoopVal(root.getBelongingFunc()).add(subloop);
                }
                int depth = 1;
                LoopVal tmp = subloop.getParentLoop();
                while (tmp != null) {
                    tmp = tmp.getParentLoop();
                    depth++;
                }
                subloop.setLoopDepth(depth);
            }
            while (subloop != null) {
                subloop.addBlock(block);
                subloop = subloop.getParentLoop();
            }
            for (BasicBlock outBasicBlock : block.getBlockOutBasicBlock()) {
                if (!visited.contains(outBasicBlock)) {
                    stack.push(outBasicBlock);
                }
            }
        }
    }
}