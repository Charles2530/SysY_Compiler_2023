package syntax.decl;

import generation.utils.ErrorController;
import syntax.AstNode;
import syntax.utils.Definer;
import syntax.utils.Judge;

import java.io.IOException;

public class Decl {
    private final AstNode rootAst;

    public Decl(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        this.analysis();
    }

    public void analysis() throws IOException {
        AstNode declNode = new AstNode("<Decl>");
        rootAst.addChild(declNode);
        if (Judge.isConstDecl()) {
            Definer.genConstDecl(declNode);
        } else if (Judge.isVarDecl()) {
            Definer.genVarDecl(declNode);
        } else {
            ErrorController.printDeclPrintError();
        }
    }
}
