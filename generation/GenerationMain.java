package generation;

import generation.llvm.LLvmGenIR;
import syntax.AstNode;

public class GenerationMain {
    private AstNode rootAst;
    private LLvmGenIR llvmGenIR;
    private static Module module;

    public GenerationMain(AstNode rootAst) {
        this.rootAst = rootAst;
        GenerationMain.module = new Module();
    }

    public void generate() {
        LLvmGenIR llvmGenIR = new LLvmGenIR(rootAst);
        this.llvmGenIR = llvmGenIR;
        preTraverse(rootAst);
        printLlvm(module);
    }

    private void preTraverse(AstNode rootAst) {
        for (AstNode astNode : rootAst.getChildList()) {
            preTraverse(astNode);
        }
        llvmGenIR.genIrAnalysis(rootAst);
    }

    private void printLlvm(Module module) {

    }

    public static Module getModule() {
        return module;
    }
}
