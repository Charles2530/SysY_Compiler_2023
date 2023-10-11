package generation;

import generation.llvm.LLvmGenIR;
import generation.utils.IrNameController;
import syntax.AstNode;

public class GenerationMain {
    private final AstNode rootAst;
    private static LLvmGenIR llvmGenIR;
    private static Module module;

    public GenerationMain(AstNode rootAst) {
        this.rootAst = rootAst;
        GenerationMain.module = new Module();
        IrNameController.init();
    }

    public void generate() {
        GenerationMain.llvmGenIR = new LLvmGenIR();
        GenerationMain.llvmGenIR.genIrAnalysis(rootAst);
        printLlvm(module);
    }

    public static void preTraverse(AstNode rootAst) {
        if (rootAst.isLeaf()) {
            return;
        }
        for (AstNode astNode : rootAst.getChildList()) {
            GenerationMain.llvmGenIR.genIrAnalysis(astNode);
        }
    }

    private void printLlvm(Module module) {

    }

    public static Module getModule() {
        return module;
    }
}
