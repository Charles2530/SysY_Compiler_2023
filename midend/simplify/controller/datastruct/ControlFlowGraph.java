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
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> indBasicBlockMap;
    private static HashMap<BasicBlock, ArrayList<BasicBlock>> outBasicBlockMap;

    public static void build(Module module) {
        for (Function function : module.getFunctions()) {
            ControlFlowGraph.indBasicBlockMap = new HashMap<>();
            ControlFlowGraph.outBasicBlockMap = new HashMap<>();
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                ControlFlowGraph.indBasicBlockMap.put(basicBlock, new ArrayList<>());
                ControlFlowGraph.outBasicBlockMap.put(basicBlock, new ArrayList<>());
            }
            ControlFlowGraph.buildControlFlowGraph(function);
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                basicBlock.setIndBasicBlocks(ControlFlowGraph.indBasicBlockMap.get(basicBlock));
                basicBlock.setOutBasicBlocks(ControlFlowGraph.outBasicBlockMap.get(basicBlock));
            }
            function.setIndBasicBlocks(ControlFlowGraph.indBasicBlockMap);
            function.setOutBasicBlocks(ControlFlowGraph.outBasicBlockMap);
        }
    }

    private static void buildControlFlowGraph(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            Instr lastInstr = basicBlock.getLastInstr();
            if (lastInstr instanceof JumpInstr jumpInstr) {
                BasicBlock targetBasicBlock = jumpInstr.getTarget();
                ControlFlowGraph.addIndBasicBlock(targetBasicBlock, basicBlock);
                ControlFlowGraph.addOutBasicBlock(basicBlock, targetBasicBlock);
            } else if (lastInstr instanceof BrInstr brInstr) {
                BasicBlock thenBasicBlock = brInstr.getThenBlock();
                ControlFlowGraph.addIndBasicBlock(thenBasicBlock, basicBlock);
                ControlFlowGraph.addOutBasicBlock(basicBlock, thenBasicBlock);
                BasicBlock elseBasicBlock = brInstr.getElseBlock();
                ControlFlowGraph.addIndBasicBlock(elseBasicBlock, basicBlock);
                ControlFlowGraph.addOutBasicBlock(basicBlock, elseBasicBlock);
            }
        }
    }

    private static void addIndBasicBlock(BasicBlock basicBlock, BasicBlock indBasicBlock) {
        ControlFlowGraph.indBasicBlockMap.get(basicBlock).add(indBasicBlock);
    }

    private static void addOutBasicBlock(BasicBlock basicBlock, BasicBlock outBasicBlock) {
        ControlFlowGraph.outBasicBlockMap.get(basicBlock).add(outBasicBlock);
    }
}
