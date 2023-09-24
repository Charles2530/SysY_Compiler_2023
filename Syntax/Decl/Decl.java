package Syntax.Decl;

import LLVM.ErrorController;
import Syntax.AstNode;
import Syntax.Definer;

public class Decl {
    private final AstNode rootAst;

    public Decl(AstNode rootAst) {
        this.rootAst = rootAst;
        this.analysis();
    }

    public void analysis() {
        AstNode declNode = new AstNode("<Decl>");
        rootAst.addChild(declNode);
        if (Definer.IsConstDecl()) {
            Definer.ConstDecl(declNode);
        } else if (Definer.IsVarDecl()) {
            Definer.VarDecl(declNode);
        } else {
            ErrorController.DeclPrintError();
        }
    }
}
