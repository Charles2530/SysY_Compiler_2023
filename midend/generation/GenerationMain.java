package midend.generation;

import iostream.structure.OptimizerUnit;
import midend.generation.llvm.LLvmGenIR;
import midend.generation.utils.IrNameController;
import midend.generation.value.construction.Module;
import iostream.OutputController;
import frontend.generation.semantic.symtable.SymbolTable;
import frontend.generation.syntax.AstNode;

/**
 * GenerationMain 用于生成 LLVM IR 的主类.
 */
public class GenerationMain {
    /**
     * rootAst 是语法树的根节点
     * llvmGenIR 是生成 LLVM IR 的核心解释器
     * module 是 LLVM IR 的顶级模块
     */
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

    /**
     * generate() 生成LLVM IR代码的主方法
     */
    public void generate() {
        OptimizerUnit.setIsOptimizer(false);
        GenerationMain.llvmGenIR = new LLvmGenIR();
        GenerationMain.llvmGenIR.genIrAnalysis(rootAst);
        OutputController.generationPrint(module.toString());
        OptimizerUnit.setIsOptimizer(true);
    }

    /**
     * preTraverse() 用于遍历语法树，生成 LLVM IR 代码
     */
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

}
