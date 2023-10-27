package midend.simplify.method;

import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;
import midend.simplify.controller.ControlFlowGraphController;

import java.util.Iterator;

public class Mem2RegUnit {
    private static Module module;
    private static ControlFlowGraphController cfGraphController;
    private static BasicBlock initialBasicBlock;

    private static void init() {
        Mem2RegUnit.cfGraphController = new ControlFlowGraphController(module);
        Mem2RegUnit.cfGraphController.build();
    }

    public static void run(Module module) {
        Mem2RegUnit.module = module;
        Mem2RegUnit.init();
        Mem2RegUnit.insertPhiProcess();
    }

    private static void insertPhiProcess() {
        for (Function function : module.getFunctions()) {
            Mem2RegUnit.initialBasicBlock = function.getBasicBlocks().get(0);
            function.insertPhiProcess();
        }
    }

    public static void reConfig(Instr instr) {
    }

    public static void insertPhi(Instr instr) {
    }

    public static void varRename() {
        int cnt = 0;
        Iterator<Instr> iter = initialBasicBlock.getInstrArrayList().iterator();
        while (iter.hasNext()) {
            Instr instr = iter.next();
        }
    }
}
