package frontend.generation.syntax.decl;

import iostream.ErrorController;
import frontend.generation.syntax.AstNode;
import frontend.generation.syntax.utils.Definer;
import frontend.generation.syntax.utils.Judge;

import java.io.IOException;

public class Decl {
    private final AstNode rootAst;

    public Decl(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        this.analysis();
    }

    /**
     * decl : constDecl | varDecl;
     */
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
