package backend.generation;

import backend.generation.utils.AssemblyData;
import backend.generation.utils.AssemblyUnit;
import iostream.OutputController;
import midend.generation.value.construction.Module;

public class AssemblyGeneration {
    private final Module module;
    private final AssemblyData assemblyData;

    public AssemblyGeneration(Module module) {
        this.module = module;
        this.assemblyData = new AssemblyData();
    }

    public void generate() {
        new AssemblyUnit().init();
        module.generateAssembly();
        OutputController.assemblyPrint(assemblyData);
    }
}
