package frontend.syntax.funcdef;

import iostream.ErrorController;
import iostream.ErrorToken;
import frontend.syntax.AstNode;
import frontend.syntax.AstRecursion;
import frontend.syntax.utils.Definer;
import frontend.syntax.utils.Judge;

import java.io.IOException;

public class FuncDef {
    private final AstNode rootAst;

    public FuncDef(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() throws IOException {
        AstNode funcDefNode = new AstNode("<FuncDef>");
        rootAst.addChild(funcDefNode);
        if (Judge.isFuncType()) {
            genFuncType(funcDefNode);
        } else {
            ErrorController.printFuncDefPrintError();
        }
        if (Judge.isIdent()) {
            Definer.genIdent(funcDefNode);
        } else {
            ErrorController.printFuncDefPrintError();
        }
        if (getPreSym().equals("LPARENT")) {
            AstNode lparentNode = new AstNode("LPARENT");
            funcDefNode.addChild(lparentNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printFuncDefPrintError();
        }
        if (Judge.isFuncFParams()) {
            genFuncFParams(funcDefNode);
        }
        if (getPreSym().equals("RPARENT")) {
            AstNode rparentNode = new AstNode("RPARENT");
            funcDefNode.addChild(rparentNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.addError(new ErrorToken("j",
                    AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
        }
        if (Judge.isBlock()) {
            Definer.genBlock(funcDefNode);
        } else {
            ErrorController.printFuncDefPrintError();
        }
    }

    private void genFuncType(AstNode funcDefNode) throws IOException {
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
            ErrorController.printFuncDefPrintError();
        }
    }

    private void genFuncFParams(AstNode funcDefNode) throws IOException {
        AstNode funcFParamsNode = new AstNode("<FuncFParams>");
        funcDefNode.addChild(funcFParamsNode);
        while (Judge.isFuncFParam()) {
            genFuncFParam(funcFParamsNode);
            if (getPreSym().equals("COMMA")) {
                AstNode commaNode = new AstNode("COMMA");
                funcFParamsNode.addChild(commaNode);
                AstRecursion.nextSym();
            } else {
                break;
            }
        }
    }

    private void genFuncFParam(AstNode funcFParamsNode) throws IOException {
        AstNode funcFParamNode = new AstNode("<FuncFParam>");
        funcFParamsNode.addChild(funcFParamNode);
        if (getPreSym().equals("INTTK")) {
            AstNode btypeNode = new AstNode("<BType>");
            funcFParamNode.addChild(btypeNode);
            AstNode intNode = new AstNode("INTTK");
            btypeNode.addChild(intNode);
            AstRecursion.nextSym();
        } else {
            ErrorController.printFuncDefPrintError();
        }
        if (Judge.isIdent()) {
            Definer.genIdent(funcFParamNode);
        } else {
            ErrorController.printFuncDefPrintError();
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
                ErrorController.addError(new ErrorToken("k",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
        while (getPreSym().equals("LBRACK")) {
            AstNode lbrackNode = new AstNode("LBRACK");
            funcFParamNode.addChild(lbrackNode);
            AstRecursion.nextSym();
            if (Judge.isConstExp()) {
                Definer.genConstExp(funcFParamNode);
            } else {
                ErrorController.printFuncDefPrintError();
            }
            if (getPreSym().equals("RBRACK")) {
                AstNode rbrackNode = new AstNode("RBRACK");
                funcFParamNode.addChild(rbrackNode);
                AstRecursion.nextSym();
            } else {
                ErrorController.addError(new ErrorToken("k",
                        AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine()));
            }
        }
    }

    private static String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
