package midend.simplify.method;

import midend.generation.value.construction.Module;
import midend.generation.value.construction.user.Function;

public class DeadCodeEliminationUnit {
    public static void run(Module module) {
        for (Function function : module.getFunctions()) {
            function.deadCodeElimination();
        }
    }
}
