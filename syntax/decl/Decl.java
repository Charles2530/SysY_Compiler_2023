package syntax.decl;

import llvm.ErrorController;
import syntax.AstNode;
import syntax.handler.Definer;
import syntax.handler.Judge;

public class Decl {
    private final AstNode rootAst;

    public Decl(AstNode rootAst) {
        this.rootAst = rootAst;
        this.analysis();
    }

    public void analysis() {
        AstNode declNode = new AstNode("<Decl>");
        rootAst.addChild(declNode);
        if (Judge.IsConstDecl()) {
            Definer.ConstDecl(declNode);
        } else if (Judge.IsVarDecl()) {
            Definer.VarDecl(declNode);
        } else {
            ErrorController.DeclPrintError();
        }
    }
}
