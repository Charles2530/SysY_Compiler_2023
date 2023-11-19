package midend.simplify.controller.datastruct;

import iostream.structure.DebugDetailController;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.JumpInstr;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ControlFlowGraph 是控制流图，
 * 主要用于控制流图的构建，
 * 属于一种数据结构
 */
public class ControlFlowGraph {
    /**
     * indBasicBlockFunctionMap 存储了每个函数的每个基本块的前序基本块
     * outBasicBlockFunctionMap 存储了每个函数的每个基本块的后序基本块
     * indBasicBlockMap 存储了每个基本块的前序基本块
     * outBasicBlockMap 存储了每个基本块的后序基本块
     */
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> indBasicBlockFunctionMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> outBasicBlockFunctionMap;

    /**
     * build() 是控制流图的构建函数，
     * 是构建控制流图的主函数
     */
    public static void build(Module module) {
        ControlFlowGraph.indBasicBlockFunctionMap = new HashMap<>();
        ControlFlowGraph.outBasicBlockFunctionMap = new HashMap<>();
        for (Function function : module.getFunctions()) {
            HashMap<BasicBlock, ArrayList<BasicBlock>> indBasicBlockMap = new HashMap<>();
            ControlFlowGraph.addFunctionIndBasicBlock(function, indBasicBlockMap);
            HashMap<BasicBlock, ArrayList<BasicBlock>> outBasicBlockMap = new HashMap<>();
            ControlFlowGraph.addFunctionOutBasicBlock(function, outBasicBlockMap);
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                ControlFlowGraph.addBlockIndBasicBlockList(basicBlock, new ArrayList<>());
                ControlFlowGraph.addBlockOutBasicBlockList(basicBlock, new ArrayList<>());
            }
            ControlFlowGraph.buildControlFlowGraph(function);
        }
        DebugDetailController.printControlFlowGraph(
                indBasicBlockFunctionMap, outBasicBlockFunctionMap);
    }

    /**
     * buildControlFlowGraph 方法用于构建控制流图
     * 这里求解出了IndBasicBlock和OutBasicBlock
     */
    private static void buildControlFlowGraph(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            Instr lastInstr = basicBlock.getLastInstr();
            if (lastInstr instanceof JumpInstr jumpInstr) {
                ControlFlowGraph.addDoubleEdge(basicBlock, jumpInstr.getTarget());
            } else if (lastInstr instanceof BrInstr brInstr) {
                ControlFlowGraph.addDoubleEdge(basicBlock, brInstr.getThenBlock());
                ControlFlowGraph.addDoubleEdge(basicBlock, brInstr.getElseBlock());
            }
        }
    }

    /**
     * addDoubleEdge 方法用于向控制流图中添加双向边，
     * 表示两个基本块之间的前序和后序关系
     */
    public static void addDoubleEdge(BasicBlock fromBlock, BasicBlock toBlock) {
        ControlFlowGraph.addBlockIndBasicBlock(toBlock, fromBlock);
        ControlFlowGraph.addBlockOutBasicBlock(fromBlock, toBlock);
    }

    public static void addFunctionIndBasicBlock(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> indBasicBlockMap) {
        ControlFlowGraph.indBasicBlockFunctionMap.put(function, indBasicBlockMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionIndBasicBlock(Function function) {
        return ControlFlowGraph.indBasicBlockFunctionMap.get(function);
    }

    public static void addFunctionOutBasicBlock(
            Function function, HashMap<BasicBlock, ArrayList<BasicBlock>> outBasicBlockMap) {
        ControlFlowGraph.outBasicBlockFunctionMap.put(function, outBasicBlockMap);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionOutBasicBlock(Function function) {
        return ControlFlowGraph.outBasicBlockFunctionMap.get(function);
    }

    /**
     * addBlockIndBasicBlock 方法用于向控制流图中添加前序基本块
     *
     * @param basicBlock    基本块
     * @param indBasicBlock 前序基本块
     * @param idx           前序基本块的插入基本块集合的位置
     *                      若缺省，则直接插入到基本块集合的末尾
     */
    public static void addBlockIndBasicBlock(BasicBlock basicBlock,
                                             BasicBlock indBasicBlock, int... idx) {
        ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .computeIfAbsent(basicBlock, k -> new ArrayList<>());
        if (idx.length == 1) {
            ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                    .get(basicBlock).add(idx[0], indBasicBlock);
        } else {
            if (!ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                    .get(basicBlock).contains(indBasicBlock)) {
                ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                        .get(basicBlock).add(indBasicBlock);
            }
        }
    }

    public static void addBlockOutBasicBlockList(
            BasicBlock basicBlock, ArrayList<BasicBlock> outBasicBlocks) {
        if (!ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .containsKey(basicBlock)) {
            ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                    .put(basicBlock, outBasicBlocks);
        }
    }

    /**
     * addBlockOutBasicBlock 方法用于向控制流图中添加后序基本块
     *
     * @param basicBlock    基本块
     * @param outBasicBlock 后序基本块
     * @param idx           后序基本块的插入基本块集合的位置
     *                      若缺省，则直接插入到基本块集合的末尾
     */
    public static void addBlockOutBasicBlock(BasicBlock basicBlock,
                                             BasicBlock outBasicBlock, int... idx) {
        ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .computeIfAbsent(basicBlock, k -> new ArrayList<>());
        if (idx.length == 1) {
            ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                    .get(basicBlock).add(idx[0], outBasicBlock);
        } else {
            if (!ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                    .get(basicBlock).contains(outBasicBlock)) {
                ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                        .get(basicBlock).add(outBasicBlock);
            }
        }
    }

    public static void addBlockIndBasicBlockList(
            BasicBlock basicBlock, ArrayList<BasicBlock> indBasicBlocks) {
        if (!ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .containsKey(basicBlock)) {
            ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                    .put(basicBlock, indBasicBlocks);
        }
    }

    public static ArrayList<BasicBlock> getBlockIndBasicBlock(BasicBlock basicBlock) {
        ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .computeIfAbsent(basicBlock, k -> new ArrayList<>());
        return ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static ArrayList<BasicBlock> getBlockOutBasicBlock(BasicBlock basicBlock) {
        ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .computeIfAbsent(basicBlock, k -> new ArrayList<>());
        return ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    /**
     * insertBlockIntoGraph 方法用于向控制流图中插入基本块
     * 即在indBasicBlock和outBasicBlock之间插入midBasicBlock
     * 主要用于在控制流图中插入基本块,这里用于消除PhiInstr，
     * 便于后续形成MIPS汇编
     *
     * @param indbasicBlock 前序基本块
     * @param outbasicblock 后序基本块
     * @param midbasicblock 中间基本块
     */
    public static void insertBlockIntoGraph(BasicBlock indbasicBlock,
                                            BasicBlock outbasicblock, BasicBlock midbasicblock) {
        ControlFlowGraph.addBlockIndBasicBlock(outbasicblock, midbasicblock,
                ControlFlowGraph.getBlockIndBasicBlock(outbasicblock).indexOf(indbasicBlock));
        ControlFlowGraph.addBlockOutBasicBlock(indbasicBlock, midbasicblock,
                ControlFlowGraph.getBlockOutBasicBlock(indbasicBlock).indexOf(outbasicblock));
        ControlFlowGraph.getBlockIndBasicBlock(outbasicblock).remove(indbasicBlock);
        ControlFlowGraph.getBlockOutBasicBlock(indbasicBlock).remove(outbasicblock);
        if (!ControlFlowGraph.getFunctionIndBasicBlock(midbasicblock.getBelongingFunc()).
                containsKey(midbasicblock)) {
            ControlFlowGraph.addBlockIndBasicBlockList(midbasicblock, new ArrayList<>());
            ControlFlowGraph.addBlockIndBasicBlock(midbasicblock, indbasicBlock);
            ControlFlowGraph.addBlockOutBasicBlockList(midbasicblock, new ArrayList<>());
            ControlFlowGraph.addBlockOutBasicBlock(midbasicblock, outbasicblock);
        }
    }

    /**
     * modifyMerged 方法用于修改合并后的基本块
     * 即在两个块合并后修改对应的indBasicBlock和outBasicBlock
     * 这里主要用于在后端优化时跳转基本块的合并
     */
    public static void modifyMerged(BasicBlock preBasicBlock, BasicBlock basicBlock) {
        preBasicBlock.getBlockOutBasicBlock().remove(basicBlock);
        basicBlock.getBlockOutBasicBlock().forEach(outBasicBlock -> {
            preBasicBlock.getBlockOutBasicBlock().add(outBasicBlock);
            outBasicBlock.getBlockIndBasicBlock().remove(basicBlock);
            outBasicBlock.getBlockIndBasicBlock().add(preBasicBlock);
        });
    }
}
