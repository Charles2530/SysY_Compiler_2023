package Syntax.FuncDef;

import LLVM.ErrorController;
import Syntax.AstNode;
import Syntax.AstRecursion;
import Syntax.Handler.Definer;
import Syntax.Handler.Judge;

public class FuncDef {
    private AstNode rootAst;

    public FuncDef(AstNode rootAst) {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() {
        AstNode funcDefNode = new AstNode("<FuncDef>");
        rootAst.addChild(funcDefNode);
        if (Judge.IsFuncType()) {
            FuncType(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Judge.IsIdent()) {
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
        if (Judge.IsFuncFParams()) {
            FuncFParams(funcDefNode);
        }
        if (getPreSym().equals("RPARENT")) {
            AstNode rparentNode = new AstNode("RPARENT");
            funcDefNode.addChild(rparentNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Judge.IsBlock()) {
            Definer.Block(funcDefNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
    }

    private void FuncType(AstNode funcDefNode) {
        AstNode funcTypeNode = new AstNode("<FuncType>");
        funcDefNode.addChild(funcTypeNode);
        if (getPreSym().equals("INTTK")) {
            AstNode intNode = new AstNode("INTTK");
            funcTypeNode.addChild(intNode);
            AstRecursion.nextSym();
        } else if (getPreSym().equals("VOIDTK")) {
            AstNode voidNode = new AstNode("VOIDTK");
            funcTypeNode.addChild(voidNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
    }

    private void FuncFParams(AstNode funcDefNode) {
        AstNode funcFParamsNode = new AstNode("<FuncFParams>");
        funcDefNode.addChild(funcFParamsNode);
        while (Judge.IsFuncFParam()) {
            FuncFParam(funcFParamsNode);
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                funcFParamsNode.addChild(commaNode);
                AstRecursion.nextSym();
            } else {
                break;
            }
        }
    }

    private void FuncFParam(AstNode funcFParamsNode) {
        AstNode funcFParamNode = new AstNode("<FuncFParam>");
        funcFParamsNode.addChild(funcFParamNode);
        if (getPreSym().equals("INTTK")) {
            AstNode bTypeNode = new AstNode("<BType>");
            funcFParamNode.addChild(bTypeNode);
            AstNode intNode = new AstNode("INTTK");
            bTypeNode.addChild(intNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (Judge.IsIdent()) {
            Definer.Ident(funcFParamNode);
        } else {
            ErrorController.FuncDefPrintError();
        }
        if (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            funcFParamNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                funcFParamNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.FuncDefPrintError();
            }
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            funcFParamNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.IsConstExp()) {
                Definer.ConstExp(funcFParamNode);
            } else {
                ErrorController.FuncDefPrintError();
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                funcFParamNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.FuncDefPrintError();
            }
        }
    }

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
