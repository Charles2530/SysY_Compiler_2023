package syntax.mainFuncDef;

import generation.ErrorController;
import syntax.AstNode;
import syntax.AstRecursion;
import syntax.handler.Definer;
import syntax.handler.Judge;

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
                        if (Judge.IsBlock()) {
                            Definer.Block(mainFuncDefNode);
                        } else {
                            ErrorController.MainFuncDefPrintError();
                        }
                    } else {
                        new ErrorController("j", AstRecursion.getNextSymToken(-1).getLineNum());
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
