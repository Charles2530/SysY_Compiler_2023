package generation;

import syntax.AstNode;

public class GenerationMain {
    private AstNode rootAst;
    private LLvmGenIR llvmGenIR;

    public GenerationMain(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    public void generate() {
        LLvmGenIR llvmGenIR = new LLvmGenIR(rootAst);
        this.llvmGenIR = llvmGenIR;
        preTraverse(rootAst);
    }

    private void preTraverse(AstNode rootAst) {
        for (AstNode astNode : rootAst.getChildList()) {
            preTraverse(astNode);
        }
        llvmGenIR.genIrAnalysis(rootAst);
    }
}
