package midend.simplify.controller;

import midend.generation.value.construction.Module;
import midend.simplify.controller.datastruct.ControlFlowGraph;
import midend.simplify.controller.datastruct.DominatorTree;

public class ControlFlowGraphController {
    private final Module module;

    public ControlFlowGraphController(Module module) {
        this.module = module;
    }

    public void build() {
        ControlFlowGraph.build(module);
        DominatorTree.build(module);
    }
}
