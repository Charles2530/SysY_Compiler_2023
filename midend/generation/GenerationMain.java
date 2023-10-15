package midend.generation;

import midend.generation.llvm.LLvmGenIR;
import midend.generation.utils.IrNameController;
import midend.generation.value.construction.Module;
import iostream.OutputController;
import frontend.semantic.symtable.SymbolTable;
import frontend.syntax.AstNode;

public class GenerationMain {
    private final AstNode rootAst;
    private static LLvmGenIR llvmGenIR;
    private static Module module;

    public GenerationMain(AstNode rootAst) {
        this.rootAst = rootAst;
        GenerationMain.module = new Module();
        SymbolTable.clear();
        IrNameController.init(module);
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
            if (astNode.getGrammarType().matches(
                    "IFTK|FORTK|BREAKTK|CONTINUETK|RETURNTK|PRINTFTK|ASSIGN")) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
