package frontend.simplify;

import frontend.generation.syntax.AstNode;
import frontend.simplify.method.FunctionInlineUnit;
import iostream.OptimizerUnit;

public class FrontEndOptimizerUnit extends OptimizerUnit {
    private final AstNode rootAst;
    private final boolean isFunctionInline = true;

    public FrontEndOptimizerUnit(AstNode rootAst) {
        this.rootAst = rootAst;
    }

    @Override
    public void optimize() {
        if (isFunctionInline) {
            FunctionInlineUnit.run(rootAst);
        }
    }
}
