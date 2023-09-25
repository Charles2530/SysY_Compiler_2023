package Syntax.MainFuncDef;

import LLVM.ErrorController;
import Syntax.AstNode;
import Syntax.AstRecursion;
import Syntax.Handler.Definer;

public class MainFuncDef {
    private AstNode rootAst;

    public MainFuncDef(AstNode rootAst) {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() {
        AstNode mainFuncDefNode = new AstNode("<MainFuncDef>");
        rootAst.addChild(mainFuncDefNode);
        if (getPreSym().equals("INTTK")) {
            AstNode intNode = new AstNode("INTTK");
            mainFuncDefNode.addChild(intNode);
            AstRecursion.nextSym();
            if (getPreSym().equals("MAINTK")) {
                AstNode mainNode = new AstNode("MAINTK");
                mainFuncDefNode.addChild(mainNode);
                AstRecursion.nextSym();
                if (getPreSym().equals("LPARENT")) {
                    AstNode lparentNode = new AstNode("LPARENT");
                    mainFuncDefNode.addChild(lparentNode);
                    AstRecursion.nextSym();
                    if (getPreSym().equals("RPARENT")) {
                        AstNode rparentNode = new AstNode("RPARENT");
                        mainFuncDefNode.addChild(rparentNode);
                        AstRecursion.nextSym();
                        Definer.Block(mainFuncDefNode);
                    } else {
                        ErrorController.MainFuncDefPrintError();
                    }
                } else {
                    ErrorController.MainFuncDefPrintError();
                }
            } else {
                ErrorController.MainFuncDefPrintError();
            }
        } else {
            ErrorController.MainFuncDefPrintError();
        }
    }

    private String getPreSym() {
        return AstRecursion.getPreSymToken().getReservedWord();
    }
}
