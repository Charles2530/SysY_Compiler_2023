package Syntax.FuncDef;

import LLVM.ErrorController;
import Syntax.AstNode;
import Syntax.AstRecursion;
import Syntax.Definer;

public class FuncDef {
    private AstNode rootAst;

    public FuncDef(AstNode rootAst) {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() {
        AstNode funcDefNode = new AstNode("<FuncDef>");
        rootAst.addChild(funcDefNode);
        if (IsFuncType()) {
            FuncType(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Definer.IsIdent()) {
            Definer.Ident(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            funcDefNode.addChild(lparentNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (IsFuncFParams()) {
            FuncFParams(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (getPreSym().equals("RPARENT")) {
            AstNode rparentNode = new AstNode("RPARENT");
            funcDefNode.addChild(rparentNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Definer.IsBlock()) {
            Definer.Block(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
    }

    private boolean IsFuncFParams() {
        return IsFuncFParam();
    }

    private boolean IsFuncType() {
        return getPreSym().equals("INTTK") || getPreSym().equals("VOIDTK");
    }

    private void FuncFParams(AstNode funcDefNode) {
        while (IsFuncFParam()) {
            FuncFParam(funcDefNode);
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                funcDefNode.addChild(commaNode);
                AstRecursion.nextSym();
                FuncFParams(funcDefNode);
            } else {
                break;
            }
        }
    }

    private void FuncFParam(AstNode funcDefNode) {
        if (getPreSym().equals("INTTK")) {
            AstNode bTypeNode = new AstNode("<BType>");
            funcDefNode.addChild(bTypeNode);
            AstNode intNode = new AstNode("INTTK");
            bTypeNode.addChild(intNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Definer.IsIdent()) {
            Definer.Ident(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            funcDefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                funcDefNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.FuncDefPrintError();
            }
        } else {
            ErrorController.FuncDefPrintError();
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            funcDefNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Definer.IsConstExp()) {
                Definer.ConstExp(funcDefNode);
            } else {
                ErrorController.FuncDefPrintError();
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                funcDefNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.FuncDefPrintError();
            }
        }
    }

    private boolean IsFuncFParam() {
        return getPreSym().equals("INTTK");
    }

    private void FuncType(AstNode funcDefNode) {
        if (getPreSym().equals("INTTK")) {
            AstNode intNode = new AstNode("INTTK");
            funcDefNode.addChild(intNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("VOIDTK")) {
            AstNode voidNode = new AstNode("VOIDTK");
            funcDefNode.addChild(voidNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
    }

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
