package frontend.simplify.method;

import frontend.generation.syntax.AstNode;

public class FunctionInlineUnit {
    public static void run(AstNode rootAst) {
        inlineAnalysis(rootAst);
    }

    private static void inlineAnalysis(AstNode rootAst) {
        for (AstNode astNode : rootAst.getChildList()) {
            inlineAnalysis(astNode);
        }
    }
}
