package Syntax.Decl;

import Syntax.AstNode;
import Syntax.AstRecursion;

public class Decl {
    private final boolean isDebugMode;
    private final AstNode rootAst;

    public Decl(boolean isDebugMode, AstNode rootAst) {
        this.isDebugMode = isDebugMode;
        this.rootAst = rootAst;
        this.analysis();
    }

    public void analysis() {
        AstNode constDeclNode = ConstDecl();
        if (constDeclNode != null) {
            rootAst.addChild(constDeclNode);
        }
        AstNode varDeclNode = VarDecl();
        if (varDeclNode != null) {
            rootAst.addChild(varDeclNode);
        }
    }


    private AstNode ConstDecl() {
        if (AstRecursion.getPreSymToken().getReservedWord().equals("CONSTTK")) {
            AstNode constDeclNode = new AstNode("<ConstDecl>");
            AstRecursion.nextSym();
            BType();
            ConstDef();
            return constDeclNode;
        }
        return null;
    }

    private void BType() {
        if (AstRecursion.getPreSymToken().equals("INTTK")) {
            AstNode intNode = new AstNode("<BType>");
            AstNode intTkNode = new AstNode("INTTK");
            intNode.addChild(intTkNode);
        } else {
            if (isDebugMode) {
                System.out.println("Error: ConstDecl: BType: " + AstRecursion.getPreSymToken().getLineNum());
            }
        }
    }

    private void ConstDef() {
        Ident();
        if (AstRecursion.getPreSymToken().getReservedWord().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            AstNode intNode = new AstNode("<ConstExp>");
            intNode.addChild(lbrackNode);
            ConstExp();
        }
    }

    private void ConstExp() {
    }

    private void Ident() {
    }

    private AstNode VarDecl() {
        return null;
    }

}
