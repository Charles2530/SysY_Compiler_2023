package midend.simplify.controller.datastruct;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.generation.value.instr.basis.BrInstr;
import midend.generation.value.instr.basis.JumpInstr;

import java.util.ArrayList;
import java.util.HashMap;

public class ControlFlowGraph {
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> indBasicBlockFunctionMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> indBasicBlockMap;
    private static HashMap<Function, HashMap<BasicBlock,
            ArrayList<BasicBlock>>> outBasicBlockFunctionMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> outBasicBlockMap;

    public static void build(Module module) {
        ControlFlowGraph.indBasicBlockFunctionMap = new HashMap<>();
        ControlFlowGraph.outBasicBlockFunctionMap = new HashMap<>();
        for (Function function : module.getFunctions()) {
            ControlFlowGraph.indBasicBlockMap = new HashMap<>();
            ControlFlowGraph.indBasicBlockFunctionMap
                    .put(function, ControlFlowGraph.indBasicBlockMap);
            ControlFlowGraph.outBasicBlockMap = new HashMap<>();
            ControlFlowGraph.outBasicBlockFunctionMap
                    .put(function, ControlFlowGraph.outBasicBlockMap);
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                ControlFlowGraph.indBasicBlockMap.put(basicBlock, new ArrayList<>());
                ControlFlowGraph.outBasicBlockMap.put(basicBlock, new ArrayList<>());
            }
            ControlFlowGraph.buildControlFlowGraph(function);
        }
    }

    private static void buildControlFlowGraph(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            Instr lastInstr = basicBlock.getLastInstr();
            if (lastInstr instanceof JumpInstr jumpInstr) {
                BasicBlock targetBasicBlock = jumpInstr.getTarget();
                ControlFlowGraph.addBlockIndBasicBlock(targetBasicBlock, basicBlock);
                ControlFlowGraph.addBlockOutBasicBlock(basicBlock, targetBasicBlock);
            } else if (lastInstr instanceof BrInstr brInstr) {
                BasicBlock thenBasicBlock = brInstr.getThenBlock();
                ControlFlowGraph.addBlockIndBasicBlock(thenBasicBlock, basicBlock);
                ControlFlowGraph.addBlockOutBasicBlock(basicBlock, thenBasicBlock);
                BasicBlock elseBasicBlock = brInstr.getElseBlock();
                ControlFlowGraph.addBlockIndBasicBlock(elseBasicBlock, basicBlock);
                ControlFlowGraph.addBlockOutBasicBlock(basicBlock, elseBasicBlock);
            }
        }
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionIndBasicBlock(Function function) {
        return ControlFlowGraph.indBasicBlockFunctionMap.get(function);
    }

    public static HashMap<BasicBlock,
            ArrayList<BasicBlock>> getFunctionOutBasicBlock(Function function) {
        return ControlFlowGraph.outBasicBlockFunctionMap.get(function);
    }

    public static void addBlockIndBasicBlock(BasicBlock basicBlock, BasicBlock indBasicBlock) {
        ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock).add(indBasicBlock);
    }

    public static void addBlockOutBasicBlock(BasicBlock basicBlock, BasicBlock outBasicBlock) {
        ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock).add(outBasicBlock);
    }

    public static ArrayList<BasicBlock> getBlockIndBasicBlock(BasicBlock basicBlock) {
        return ControlFlowGraph.getFunctionIndBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }

    public static ArrayList<BasicBlock> getBlockOutBasicBlock(BasicBlock basicBlock) {
        return ControlFlowGraph.getFunctionOutBasicBlock(basicBlock.getBelongingFunc())
                .get(basicBlock);
    }
}
