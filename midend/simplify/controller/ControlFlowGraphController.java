package midend.simplify.controller;

import midend.generation.value.construction.Module;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;

/**
 * ControlFlowGraphController 是控制流图控制器，
 * 主要用于控制流图的构建
 */
public class ControlFlowGraphController {
    /**
     * build() 是控制流图的构建函数，
     * 这里构建了CFG图和支配树
     *
     * @param module 是LLVM IR生成的顶级模块
     */
    public static void build(Module module) {
        ControlFlowGraph.build(module);
        DominatorTree.build(module);
    }
}
