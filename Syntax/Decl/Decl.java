package Syntax.Decl;

import Syntax.AstNode;
import Syntax.AstRecursion;

public class Decl {
    private final boolean isDebugMode;

    public Decl(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
        this.analysis();
    }

    private void analysis() {
        ConstDecl();
        VarDecl();
    }


    private void ConstDecl() {
        if (AstRecursion.getPreSymToken().getReservedWord().equals("CONSTTK")) {
            AstNode constNode = new AstNode("<ConstDecl>");
            AstRecursion.nextSym();
            BType();
            ConstDef();
        }
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
    }

    private void Ident() {
    }

    private void VarDecl() {
    }

}
