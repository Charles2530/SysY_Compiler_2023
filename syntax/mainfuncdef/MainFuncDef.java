package syntax.mainfuncdef;

import generation.utils.ErrorController;
import generation.utils.ErrorToken;
import syntax.AstNode;
import syntax.AstRecursion;
import syntax.utils.Definer;
import syntax.utils.Judge;

import java.io.IOException;

public class MainFuncDef {
    private final AstNode rootAst;

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
                    } else {
                        ErrorController.addError(new ErrorToken("j",
                                AstRecursion.getPreviousNoTerminalAst()
                                        .getSpan().getEndLine()));
                    }
                    if (Judge.isBlock()) {
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
