package generation;

import generation.llvm.LLvmGenIR;
import generation.utils.IrNameController;
import generation.value.construction.Module;
import iostream.OutputController;
import semantic.symtable.SymbolTable;
import syntax.AstNode;

public class GenerationMain {
    private final AstNode rootAst;
    private static LLvmGenIR llvmGenIR;
    private static Module module;

    public GenerationMain(AstNode rootAst) {
        this.rootAst = rootAst;
        GenerationMain.module = new Module();
        SymbolTable.clear();
        IrNameController.init();
    }

    public static Module getModule() {
        return module;
    }

    public void generate() {
        GenerationMain.llvmGenIR = new LLvmGenIR();
        GenerationMain.llvmGenIR.genIrAnalysis(rootAst);
        OutputController.generationPrint(module.toString());
    }

    public static void preTraverse(AstNode rootAst) {
        if (rootAst.isLeaf()) {
            return;
        }
        for (AstNode astNode : rootAst.getChildList()) {
            GenerationMain.llvmGenIR.genIrAnalysis(astNode);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
