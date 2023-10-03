package syntax.mainFuncDef;

import generation.utils.ErrorController;
import syntax.AstNode;
import syntax.AstRecursion;
import syntax.utils.Definer;
import syntax.utils.Judge;

import java.io.IOException;

public class MainFuncDef {
    private AstNode rootAst;

    public MainFuncDef(AstNode rootAst) throws IOException {
        this.rootAst = rootAst;
        this.analysis();
    }

    private void analysis() throws IOException {
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
                        new ErrorController("j",
                                AstRecursion.getPreviousNoTerminalAst().getSpan().getEndLine());
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
