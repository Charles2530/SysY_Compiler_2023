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
     * module 是LLVM IR生成的顶级模块
     */
    private final Module module;

    public ControlFlowGraphController(Module module) {
        this.module = module;
    }

    /**
     * build() 是控制流图的构建函数，
     * 这里构建了CFG图和支配树
     */
    public void build() {
        ControlFlowGraph.build(module);
        DominatorTree.build(module);
    }
}
