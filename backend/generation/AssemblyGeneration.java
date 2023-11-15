package backend.generation;

import backend.generation.utils.AssemblyData;
import backend.generation.utils.AssemblyUnit;
import iostream.structure.OutputController;
import midend.generation.value.construction.Module;

/**
 * AssemblyGeneration 是汇编生成器，
 * 主要用于生成MIPS汇编
 */
public class AssemblyGeneration {
    /**
     * module 是LLVM IR生成的顶级模块
     * assemblyData 是汇编数据
     */
    private final Module module;
    private final AssemblyData assemblyData;

    public AssemblyGeneration(Module module) {
        this.module = module;
        this.assemblyData = new AssemblyData();
    }

    /**
     * generate 方法用于生成MIPS汇编，是生成MIPS汇编的主函数
     */
    public void generate() {
        new AssemblyUnit().init();
        module.generateAssembly();
        OutputController.assemblyPrint(assemblyData);
    }
}
