package backend.simplify.method;

import midend.generation.value.instr.optimizer.MoveInstr;
import midend.generation.value.instr.optimizer.ParallelCopy;
import midend.generation.value.construction.BasicBlock;
import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;
import midend.generation.value.construction.user.Instr;

import java.util.ArrayList;

public class PhiEliminationUnit {
    public static void run(Module module) {
        module.getFunctions().forEach(Function::phiEliminate);
    }

    public static void putParallelCopy(ParallelCopy parallelCopy, BasicBlock indbasicBlock) {
    }

    public static void insertParallelCopy(ParallelCopy parallelCopy,
                                          BasicBlock indbasicBlock, BasicBlock present) {
    }

    public static void removePhiInstr(ArrayList<Instr> instrArrayList) {
    }

    public static ArrayList<MoveInstr> getMoveAsm(ParallelCopy pc) {
        return null;
    }
}
